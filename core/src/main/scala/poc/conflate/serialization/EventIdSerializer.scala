package poc.conflate.serialization

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}
import poc.conflate.stream.EventId
import spray.json._

class EventIdSerializer extends Serializer[EventId] with SprayJsonSupport{

  import DefaultJsonProtocol._

  private val stringSer = new StringSerializer()
  override def serialize(topic: String, data: EventId): Array[Byte] = {
    implicit val formatter = DefaultJsonProtocol.jsonFormat1(EventId)
    val jsonString: JsValue = data.toJson
    stringSer.serialize(topic, jsonString.compactPrint)
  }
}
