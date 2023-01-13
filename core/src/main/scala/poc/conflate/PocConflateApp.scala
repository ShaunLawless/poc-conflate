package poc.conflate

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.CoordinatedShutdown

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger
import poc.conflate.http.Server
import poc.conflate.http.routes.Health
import poc.conflate.observability.Metrics

object PocConflateApp extends App {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "poc-conflate")

  val logger: Logger = Logger("POC Conflate")
  val config: Config = ConfigFactory.load()

  lazy val routes = Health.routes
  lazy val serverIO = IO(Server.run(config.getConfig("akka.http"), routes))

  lazy val metricsIO = IO(Metrics.httpServer)

  lazy val app: IO[Unit] = for {
    _ <- serverIO
    _ <- metricsIO
  } yield logger.info("POC Conflate started")

  app.unsafeRunSync()

  CoordinatedShutdown(system).addJvmShutdownHook {
    ()
  }
}