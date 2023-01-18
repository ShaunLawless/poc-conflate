package poc.conflate.serialization

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.apache.kafka.common.serialization.{Deserializer, StringDeserializer}
import poc.conflate.stream.Payload
import spray.json._

class PayloadDeserializer extends Deserializer[Payload] with SprayJsonSupport {

  private val strDeser = new StringDeserializer()

  import DefaultJsonProtocol._
  implicit val payloadSprayProtocol: RootJsonFormat[Payload] = DefaultJsonProtocol.jsonFormat2(Payload)

  override def deserialize(topic: String, data: Array[Byte]): Payload = {
    val jsonString = strDeser.deserialize(topic, data)
    jsonString.parseJson.convertTo[Payload]
  }
}
