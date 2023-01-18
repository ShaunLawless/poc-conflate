package poc.conflate.serialization

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.apache.kafka.common.serialization.{Deserializer, StringDeserializer}
import poc.conflate.stream.{AggregatedMessage, Payload}
import spray.json._

class AggregateMessageDeserializer extends Deserializer[AggregatedMessage] with SprayJsonSupport {

  private val strDeser = new StringDeserializer()

  import DefaultJsonProtocol._

  implicit val aggSprayProtocol: RootJsonFormat[AggregatedMessage] = DefaultJsonProtocol.jsonFormat2(AggregatedMessage)

  override def deserialize(topic: String, data: Array[Byte]): AggregatedMessage = {
    val jsonString = strDeser.deserialize(topic, data)
    jsonString.parseJson.convertTo[AggregatedMessage]
  }

}
