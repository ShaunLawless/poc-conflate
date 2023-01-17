package poc.conflate.stream

import akka.actor.ActorSystem
import akka.kafka.{CommitterSettings, ConsumerSettings, ProducerSettings}
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import io.github.embeddedkafka.{EmbeddedK, EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{Producer, ProducerRecord}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import poc.conflate.serialization._

import scala.concurrent.duration._
import scala.language.postfixOps

class MappingStreamSpec extends TestKit(ActorSystem("MappingStreamSpecSys", ConfigFactory.load()))
  with AnyFlatSpecLike with Matchers with EmbeddedKafka with BeforeAndAfterAll with Eventually with IntegrationPatience with ImplicitSender{

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(30.seconds, 150.millis)

  implicit val config: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort = 9092, zooKeeperPort = 31812)

  val inboundTopic: String = "inboundMappings"
  val produceTopic: String = "outboundMappings"

  val eventIdSerializer = new EventIdSerializer()
  implicit val eventIdDeserializer = new EventIdDeserializer()
  val payloadSerializer = new PayloadSerializer()
  val payloadDeserializer = new PayloadDeserializer()
  val mappingSerializer = new MappingSerializer()
  implicit val mappingDeserializer = new MappingDeserializer()


  val testProducerSettings: ProducerSettings[EventId, Payload] = ProducerSettings(system, Some(eventIdSerializer), Some(payloadSerializer)).withClientId("test-producer")
  val testProducer: Producer[EventId, Payload] = testProducerSettings.createKafkaProducer()

  val consumerSettings: ConsumerSettings[EventId, Payload] = ConsumerSettings(system, eventIdDeserializer, payloadDeserializer).withGroupId("mapping-consumer")
    .withProperty(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, "poc.conflate.stream.ConflateConsumerInterceptor")
  val streamProducerSettings: ProducerSettings[EventId, Mapping] = ProducerSettings(system, Some(eventIdSerializer), Some(mappingSerializer)).withClientId("mapping-producer")
  val committerSettings: CommitterSettings = CommitterSettings(system)

  val kafka: EmbeddedK = EmbeddedKafka.start()


  createCustomTopic(inboundTopic, Map.empty, partitions = 3, replicationFactor = 1)
  createCustomTopic(produceTopic, Map.empty, partitions = 3, replicationFactor = 1)

  val mapperStream = MappingStream.restartableStream(
    consumerSettings,
    streamProducerSettings,
    committerSettings,
    inboundTopic
  )

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "MappingStream" should "not conflate messages when deliver rate is below throttle threshold" in {
    val key = EventId(12)
    val payload = Payload(12, List(1122))
    val payload2 = Payload(12, List(4455))
    val pr:ProducerRecord[EventId, Payload] = new ProducerRecord(inboundTopic, key, payload)
    val pr2:ProducerRecord[EventId, Payload] = new ProducerRecord(inboundTopic, key, payload2)
    Thread.sleep(3000)

    testProducer.send(pr)
    testProducer.send(pr2)

    Thread.sleep(3000)

    val consumedRecord:(EventId, Mapping) = consumeFirstKeyedMessageFrom[EventId, Mapping](produceTopic)
    consumedRecord._1 shouldBe key
    consumedRecord._2 shouldBe payload

    val consumedRecord2: (EventId, Mapping) = consumeFirstKeyedMessageFrom[EventId, Mapping](produceTopic)
    consumedRecord2._1 shouldBe key
    consumedRecord2._2 shouldBe payload2
  }

  "MappingStream" should " conflate messages when deliver rate is above throttle threshold" in {
    val key = EventId(12)
    val payload = Payload(12, List(1122))
    val payload2 = Payload(12, List(4455))
    val pr: ProducerRecord[EventId, Payload] = new ProducerRecord(inboundTopic, key, payload)
    val pr2: ProducerRecord[EventId, Payload] = new ProducerRecord(inboundTopic, key, payload2)

    Thread.sleep(5000)

    testProducer.send(pr)
    testProducer.send(pr2)
    testProducer.send(pr)
    testProducer.send(pr2)
    testProducer.send(pr)
    testProducer.send(pr2)
    testProducer.send(pr)
    testProducer.send(pr2)
    testProducer.send(pr)
    testProducer.send(pr2)
    testProducer.send(pr)
    testProducer.send(pr2)
    testProducer.send(pr)
    testProducer.send(pr2)
    testProducer.send(pr)
    testProducer.send(pr2)
    testProducer.send(pr)
    testProducer.send(pr2)
    testProducer.send(pr)
    testProducer.send(pr2)
    testProducer.send(pr)
    testProducer.send(pr2)
    testProducer.send(pr)
    testProducer.send(pr2)

    Thread.sleep(8000)

    val expected = AggregatedMessage(key.value, payload.value)
    val consumedRecord: (EventId, Mapping) = consumeFirstKeyedMessageFrom[EventId, Mapping](produceTopic)
    consumedRecord._1 shouldBe key
    consumedRecord._2 shouldBe expected

    val expected2 = AggregatedMessage(key.value, payload2.value)
    val consumedRecord2: (EventId, Mapping) = consumeFirstKeyedMessageFrom[EventId, Mapping](produceTopic)
    consumedRecord2._1 shouldBe key
    consumedRecord2._2 shouldBe expected2

  }

}
