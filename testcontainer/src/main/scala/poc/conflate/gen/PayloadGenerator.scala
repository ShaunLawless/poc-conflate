package poc.conflate.gen
import akka.kafka.ProducerMessage
import org.apache.kafka.clients.producer.ProducerRecord

object PayloadGenerator {

  def genPayload(number: Int, topic: String): ProducerMessage.Envelope[String, String, Int] =
    ProducerMessage.single(new ProducerRecord(topic, number.toString, "I am a message"), number)

}
