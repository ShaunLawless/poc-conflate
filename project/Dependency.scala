import sbt._

object Dependency {

  val AkkaVersion = "2.6.19"
  val AkkaHttpVersion = "10.2.9"
  lazy val akkaStreamVersion = "3.0.1"
  val log4jVersion = "2.14.1"
  val protoVersion = "3.11.4"

  lazy val allTestScope: String = "test, it, fun"

  lazy val akka: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % allTestScope,
    "com.typesafe.akka" %% "akka-stream-kafka-testkit" % akkaStreamVersion % "test",
    "com.typesafe.akka" %% "akka-stream-kafka" % akkaStreamVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % allTestScope
  )

  lazy val scalatest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.12" % allTestScope,
    ("org.scalatest" %% "scalatest" % "3.2.12" % "test->*").excludeAll(ExclusionRule(organization = "org.junit", name = "junit"))
  )

  lazy val logging: Seq[ModuleID] = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.11" % Runtime,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
  )

  lazy val observability: Seq[ModuleID] = Seq(
    "io.micrometer" % "micrometer-registry-prometheus" % "1.9.0",
    "io.github.mweirauch" % "micrometer-jvm-extras" % "0.2.2",
    "io.prometheus" % "simpleclient_httpserver" % "0.15.0",
    "io.opentracing" % "opentracing-api" % "0.33.0"
  )

  lazy val cats: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % "2.9.0",
    "org.typelevel" %% "cats-effect" % "3.4.4"
  )

  lazy val coreDeps: Seq[ModuleID] =
    akka ++ scalatest ++ logging ++ observability ++ cats
}