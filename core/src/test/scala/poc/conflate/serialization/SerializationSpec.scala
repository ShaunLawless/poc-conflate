package poc.conflate.serialization

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import poc.conflate.stream.{AggregatedMessage, EventId, Payload}

class SerializationSpec extends AnyFlatSpec with Matchers {

  val eventIdSerializer = new EventIdSerializer()
  val eventIdDeserializer = new EventIdDeserializer()
  val aggregatedMessageSerializer = new AggregatedMessageSerializer()
  val aggregateMessageDeserializer = new AggregateMessageDeserializer()
  val mappingSerializer = new MappingSerializer()
  val mappingDeserializer = new MappingDeserializer()

  "EventIdSerialisers" should "serialise and deserialize EventId correctly" in {
     val eventIdIn = EventId(1)
     val ser: Array[Byte] = eventIdSerializer.serialize("topic", eventIdIn)
     val eventIdOut = eventIdDeserializer.deserialize("topic", ser)
     assert(eventIdIn == eventIdOut)
  }

  "Mapping Serialisers" should "serialise and deserialize Payload correctly" in {
    val PayloadIn = Payload(1, List(10))
    val ser: Array[Byte] = mappingSerializer.serialize("topic", PayloadIn)
    val PayloadOut = mappingDeserializer.deserialize("topic", ser)
    assert(PayloadIn == PayloadOut)

    val aggIn = AggregatedMessage(1, List(10,20,30))
    val ser2: Array[Byte] = mappingSerializer.serialize("topic", aggIn)
    val aggOu = mappingDeserializer.deserialize("topic", ser2)
    assert(aggIn == aggOu)
  }

}
