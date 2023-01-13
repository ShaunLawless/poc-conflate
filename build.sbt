import sbt._
import Github._
import Release._
import Dependency.coreDeps
import com.typesafe.sbt.packager.docker.DockerVersion

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / organization := "com.flutter"
ThisBuild / scalacOptions ++= Compiler.scalacOptions

val protobufJavaVersion = "3.11.4"

releaseVersionBump := releaseBump
releaseProcess := releaseSteps
ThisBuild / coverageEnabled := false

lazy val FunTest: Configuration = config("fun").extend(Test)
lazy val funTestSettings: Seq[Def.Setting[_]] = inConfig(FunTest)(Defaults.testSettings)

lazy val core = (project in file("core")).configs(IntegrationTest, FunTest)
  .settings(
    Defaults.itSettings,
    funTestSettings,
    name := "poc-conflate",
    libraryDependencies ++= coreDeps,
    coverageMinimum := 60, //@todo this is set low for initial template installation, increase appropriately
    coverageFailOnMinimum := true,
    publishTo := Some(s"$realm".at(s"$packageRepo")),
    dockerBaseImage := "adoptopenjdk:11",
    dockerVersion := DockerVersion.parse((ThisBuild / version).value),
    dockerUpdateLatest := true,
    Docker / packageName := "poc-conflate",
    publish / aggregate := false
  )
  .dependsOn(model)
  .enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin)

lazy val model = (project in file("model")).configs(IntegrationTest, FunTest)
  .settings(
    Defaults.itSettings,
    funTestSettings,
    name := "poc-model",
    libraryDependencies ++= coreDeps,
    coverageMinimum := 60, //@todo this is set low for initial template installation, increase appropriately
    coverageFailOnMinimum := true,
    publish / aggregate := false
  )
  .enablePlugins(JavaAppPackaging)

lazy val testcontainer = (project in file("testcontainer")).configs(IntegrationTest, FunTest)
  .settings(
    Defaults.itSettings,
    funTestSettings,
    name := "poc-conflate-test-container",
    libraryDependencies ++= coreDeps,
    coverageMinimum := 60, //@todo this is set low for initial template installation, increase appropriately
    coverageFailOnMinimum := true,
    publishTo := Some(s"$realm".at(s"$packageRepo")),
    dockerBaseImage := "adoptopenjdk:11",
    dockerVersion := DockerVersion.parse((ThisBuild / version).value),
    dockerUpdateLatest := true,
    Docker / packageName := "poc-conflate-test-container",
    publish / aggregate := false
  )
  .dependsOn(model)
  .enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin)

lazy val root = (project in file("."))
  .settings(
    publishTo := Some(s"$realm".at(s"$packageRepo")),
    publish := {}
  )
  .configs(FunTest)
  .aggregate(model, core, testcontainer)

ThisBuild / publishMavenStyle := true
ThisBuild / credentials += Credentials(realm, packageRepoBase, gitHubUser, orgTokenOrElseGithubToken)
ThisBuild / resolvers += s"$realm".at(s"$packageRepo")
