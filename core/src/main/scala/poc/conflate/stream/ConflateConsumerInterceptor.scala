package poc.conflate.stream

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.consumer.{ConsumerInterceptor, ConsumerRecords, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition
import scala.jdk.CollectionConverters._

class ConflateConsumerInterceptor extends ConsumerInterceptor[EventId, Payload] with LazyLogging{

  override def onConsume(records: ConsumerRecords[EventId, Payload]): ConsumerRecords[EventId, Payload] =
    records

  override def onCommit(offsets: java.util.Map[TopicPartition, OffsetAndMetadata]): Unit =
  {
    val x = offsets
    for ( o <- offsets.values().asScala) {
      println(s"Commited Offsets were ${o.offset}")
    }

  }

  override def close(): Unit = {}

  override def configure(configs: java.util.Map[String, _]): Unit = ()

}





