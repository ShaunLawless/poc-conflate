package poc.conflate.serialization

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.apache.kafka.common.serialization.{Deserializer, StringDeserializer}
import poc.conflate.stream.EventId
import spray.json._


class EventIdDeserializer extends Deserializer[EventId] with SprayJsonSupport {

  import DefaultJsonProtocol._
  implicit val eventIdSprayProtocol: RootJsonFormat[EventId] = DefaultJsonProtocol.jsonFormat1(EventId)

  private val strDeser = new StringDeserializer()

  override def deserialize(topic: String, data: Array[Byte]): EventId = {
   val jsonStr: String = strDeser.deserialize(topic, data)
    jsonStr.parseJson.convertTo[EventId]
  }
}
