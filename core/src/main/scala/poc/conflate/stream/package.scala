package poc.conflate.stream

case class EventId(value: Int)

sealed trait Mapping {
  def id: Int
}

case class Payload(id: Int, value: List[Int]) extends Mapping

case class AggregatedMessage(id: Int, values: List[Int]) extends Mapping {
  def add(i: List[Int]): AggregatedMessage = AggregatedMessage(id, values ++ i)
}
