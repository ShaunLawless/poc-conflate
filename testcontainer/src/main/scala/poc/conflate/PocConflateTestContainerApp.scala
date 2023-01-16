package poc.conflate

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.CoordinatedShutdown
import akka.kafka.ProducerSettings
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger
import org.apache.kafka.common.serialization.StringSerializer
import poc.conflate.http.Server
import poc.conflate.http.routes.Health
import poc.conflate.observability.Metrics

import scala.concurrent.ExecutionContext

object PocConflateTestContainerApp extends App {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "poc-conflate")
  implicit val ec: ExecutionContext = system.executionContext

  val logger: Logger = Logger("POC Conflate Test Container")
  val config: Config = ConfigFactory.load()

  lazy val routes = Health.routes
  lazy val serverIO = Server.run(config.getConfig("akka.http"), routes)

  lazy val metricsIO = Metrics.httpServer

  val producerSettings: ProducerSettings[String, String] = ProducerSettings(system, new StringSerializer(), new StringSerializer())
  val topic: String = config.getString("app.topic.exchange")

  lazy val app: IO[Unit] = for {
    _ <- IO(gen.Stream.stream(producerSettings, topic).run().onComplete(_ => system.terminate()))
  } yield logger.info("POC Conflate Test Container started")

  app.unsafeRunSync()

  CoordinatedShutdown(system).addJvmShutdownHook {
    ()
  }
}