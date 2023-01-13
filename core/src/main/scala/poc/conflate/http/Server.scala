package poc.conflate.http

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import com.typesafe.config.Config
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object Server {
  val logger: Logger = Logger("HttpServer")

  def run(httpConfig: Config, routes: Route)(implicit system: ActorSystem[Nothing]): Unit = {
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val httpPort = httpConfig.getInt("server.default-http-port")
    val bindingFuture = Http().newServerAt("0.0.0.0", httpPort).bind((routes))

    bindingFuture.onComplete {
      case Success(binding) =>
        logger.info(s"Server online at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}")
      case Failure(ex) =>
        logger.info(s"Failed to bind HTTP endpoint, terminating system", ex)
    }
  }
}
