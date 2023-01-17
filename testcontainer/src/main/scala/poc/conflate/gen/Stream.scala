package poc.conflate.gen
import akka.NotUsed
import akka.kafka.ProducerMessage.MultiResultPart
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import com.typesafe.scalalogging.{LazyLogging, Logger}

import java.lang.System.Logger

object Stream extends LazyLogging{

  val src: Source[Int, NotUsed] = Source(1 to Int.MaxValue)

  def stream(producerSettings: ProducerSettings[String, String], topic: String): Source[String, NotUsed] =
    src
      .map(PayloadGenerator.genPayload(_, topic))
      .via(Producer.flexiFlow(producerSettings))
      .map {
        case ProducerMessage.Result(metadata, ProducerMessage.Message(record, passThrough)) =>
          s"${metadata.topic}/${metadata.partition} ${metadata.offset}: ${record.value}"

        case ProducerMessage.MultiResult(parts, passThrough) =>
          parts
            .map {
              case MultiResultPart(metadata, record) =>
                s"${metadata.topic}/${metadata.partition} ${metadata.offset}: ${record.value}"
            }
            .mkString(", ")

        case ProducerMessage.PassThroughResult(passThrough) =>
          s"passed through $passThrough"
      }.log("producer_result")

}
