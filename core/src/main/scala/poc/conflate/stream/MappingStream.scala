package poc.conflate.stream

import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffsetBatch}
import akka.kafka.ProducerMessage.{Message, MultiMessage}
import akka.kafka.scaladsl.{Committer, Consumer, Producer}
import akka.kafka.{CommitterSettings, ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import akka.stream.{Materializer, RestartSettings}
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.duration.DurationInt

object MappingStream {

  case class Offsets(committableOffsetBatch: CommittableOffsetBatch)

  def groupKey: CommittableMessage[EventId, Payload] => EventId = cm => cm.record.key()

  val produceTopic: String = "outboundMappings"

  def seed: CommittableMessage[EventId, Payload] => (AggregatedMessage, Offsets) = cm =>
    (AggregatedMessage(cm.record.key().value, List(cm.record.value().value).flatten), Offsets(CommittableOffsetBatch(cm.committableOffset)))

  def combine: ((AggregatedMessage, Offsets), CommittableMessage[EventId, Payload]) => (AggregatedMessage, Offsets) = {
    case ((aggMsg, offsets), cm) =>
      (aggMsg.add(cm.record.value().value), offsets.copy(offsets.committableOffsetBatch.updated(cm.committableOffset)))
  }

  def createRecord: ((Mapping, Offsets)) => Message[EventId, Mapping, CommittableOffsetBatch] = {
    case (mapping, off) =>
      val pr:ProducerRecord[EventId,Mapping] = new ProducerRecord(produceTopic, EventId(mapping.id), mapping)
      Message(pr, off.committableOffsetBatch)

  }

  private[this] val restartSettings: RestartSettings =
    RestartSettings(
      minBackoff = 3.seconds,
      maxBackoff = 30.seconds,
      randomFactor = 0.2
    ).withMaxRestarts(20, 5.minutes)

  def restartableStream(
      consumerSettings: ConsumerSettings[EventId, Payload],
      producerSettings: ProducerSettings[EventId, Mapping],
      committerSettings: CommitterSettings,
      topic: String
    )(implicit materializer: Materializer
    ) = {
    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic))
      .conflateWithSeed(seed)(combine)
      .throttle(2, 1.seconds)
      .map(createRecord)
      .via(Producer.flexiFlow(producerSettings))
      .map(_.passThrough)
      .via(Committer.batchFlow(committerSettings))
      .to(Sink.ignore)
      .run()
  }

}


