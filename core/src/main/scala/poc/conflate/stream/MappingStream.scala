package poc.conflate.stream

import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffset, CommittableOffsetBatch}
import akka.kafka.ProducerMessage.MultiMessage
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.{CommitterSettings, ConsumerMessage, ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Committer, Consumer, Producer}
import akka.stream.{Materializer, RestartSettings}
import akka.{Done, NotUsed}
import akka.stream.scaladsl.{Keep, RestartSource, Sink, Source}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.duration.DurationInt

object MappingStream {

  case class EventId(value: Int) extends AnyVal
  case class Payload()

  case class Offsets(committableOffsetBatch: CommittableOffsetBatch)

  case class AggregatedMessage()

  def groupKey: CommittableMessage[EventId, Payload] => EventId = ???

  def seed: CommittableMessage[EventId, Payload] => (AggregatedMessage, Offsets) = ???

  def combine: ((AggregatedMessage, Offsets), CommittableMessage[EventId, Payload]) => (AggregatedMessage, Offsets) = ???

  def createRecord: ((AggregatedMessage, Offsets)) => MultiMessage[EventId, AggregatedMessage, CommittableOffsetBatch] = ???

  private[this] val restartSettings: RestartSettings =
    RestartSettings(
      minBackoff = 3.seconds,
      maxBackoff = 30.seconds,
      randomFactor = 0.2
    ).withMaxRestarts(20, 5.minutes)

  def restartableStream(
      consumerSettings: ConsumerSettings[EventId, Payload],
      producerSettings: ProducerSettings[EventId, Payload],
      committerSettings: CommitterSettings,
      topic: String
    )(implicit materializer: Materializer
    ) = {
    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic))
      .conflateWithSeed(seed)(combine)
      .throttle(200, 500.milliseconds)
      .map(createRecord)
      .via(Producer.flexiFlow(producerSettings))
      .map(_.passThrough)
      .via(Committer.batchFlow(committerSettings))
      .to(Sink.ignore)
      .run()
  }

}
