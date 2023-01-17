package poc.conflate.serialization

import org.apache.kafka.common.serialization.Serializer
import poc.conflate.stream.{AggregatedMessage, Mapping, Payload}

class MappingSerializer extends Serializer[Mapping] {

  val payloadSer = new PayloadSerializer()
  val aggregatedMessageSer = new AggregatedMessageSerializer()

  def serialize(topic:String, data:Mapping): Array[Byte] = data match {
    case p: Payload => payloadSer.serialize(topic, p)
    case a: AggregatedMessage => aggregatedMessageSer.serialize(topic, a)
  }

}
