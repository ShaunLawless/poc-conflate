package poc.conflate.http.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import spray.json._

object Health extends SprayJsonSupport {

  case class Status(host: String)

  val routes: Route =
    path("start") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Application Started"))
      }
    } ~ path("liveness") {
      import DefaultJsonProtocol._
      import java.net.InetAddress
      implicit val statusFormat: RootJsonFormat[Status] = DefaultJsonProtocol.jsonFormat1(Status)

      complete(Status(InetAddress.getLocalHost.toString))
    } ~ path("readiness") {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Application Ready"))
    }

}
