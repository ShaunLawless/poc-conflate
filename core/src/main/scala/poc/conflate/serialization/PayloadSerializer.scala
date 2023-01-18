package poc.conflate.serialization


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}
import poc.conflate.stream.Payload
import spray.json.{DefaultJsonProtocol, _}

class PayloadSerializer extends Serializer[Payload] with SprayJsonSupport {

  private val stringSer = new StringSerializer()

  import DefaultJsonProtocol._

  override def serialize(topic: String, data: Payload): Array[Byte] = {
    implicit val formatter = DefaultJsonProtocol.jsonFormat2(Payload)
    val jsonString = data.toJson
    stringSer.serialize(topic, jsonString.compactPrint)
  }

}
