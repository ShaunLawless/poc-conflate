addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")
addSbtPlugin("com.github.sbt" % "sbt-release" % "1.0.15")
addSbtPlugin("com.github.cb372" % "sbt-explicit-dependencies" % "0.2.16")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.6")

addSbtPlugin("com.github.gseitz" % "sbt-protobuf" % "0.6.5")
libraryDependencies += "com.github.os72" % "protoc-jar" % "3.11.4"

libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.1"
libraryDependencies += "io.circe" %% "circe-generic" % "0.14.1"
