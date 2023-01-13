package poc.conflate.http.routes

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import akka.http.scaladsl.testkit.ScalatestRouteTest

class HealthSpec extends AnyFlatSpecLike with Matchers with ScalatestRouteTest {

  "Health" should "return OK for the route /start" in {
    Get("/start") ~> Health.routes ~> check {
      response.status.value shouldBe "200 OK"
      responseAs[String] shouldBe "Application Started"
    }
  }

  it should "return OK for the route /liveness" in {
    Get("/liveness") ~> Health.routes ~> check {
      response.status.value shouldBe "200 OK"
    }
  }

  it should "return OK for the route /readiness" in {
    Get("/readiness") ~> Health.routes ~> check {
      response.status.value shouldBe "200 OK"
      responseAs[String] shouldBe "Application Ready"
    }
  }
}