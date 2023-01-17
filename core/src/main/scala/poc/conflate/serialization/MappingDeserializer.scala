package poc.conflate.serialization

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.apache.kafka.common.serialization.{Deserializer, StringDeserializer}
import poc.conflate.stream.{AggregatedMessage, Mapping, Payload}
import spray.json._

class MappingDeserializer extends Deserializer[Mapping] with SprayJsonSupport {
  import DefaultJsonProtocol._
  implicit val aggSprayProtocol: RootJsonFormat[AggregatedMessage] = DefaultJsonProtocol.jsonFormat2(AggregatedMessage)
  implicit val payloadSprayProtocol: RootJsonFormat[Payload] = DefaultJsonProtocol.jsonFormat2(Payload)

  private val strDeser = new StringDeserializer()

  def deserialize(topic: String, data: Array[Byte]): Mapping = {
    val jsonString = strDeser.deserialize(topic, data)
    val jsValue = jsonString.parseJson
    jsValue.asJsObject().getFields("values").nonEmpty match {
      case true => jsValue.convertTo[AggregatedMessage]
      case _ => jsValue.convertTo[Payload]
    }
  }
}
