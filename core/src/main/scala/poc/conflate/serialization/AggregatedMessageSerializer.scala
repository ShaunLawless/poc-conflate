package poc.conflate.serialization

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}
import poc.conflate.stream.AggregatedMessage
import spray.json._

class AggregatedMessageSerializer extends Serializer[AggregatedMessage] with SprayJsonSupport {

  private val stringSer = new StringSerializer()

  import DefaultJsonProtocol._

  override def serialize(topic: String, data: AggregatedMessage): Array[Byte] = {
    implicit val formatter = DefaultJsonProtocol.jsonFormat2(AggregatedMessage)
    val jsonString = data.toJson
    stringSer.serialize(topic, jsonString.compactPrint)
  }

}
