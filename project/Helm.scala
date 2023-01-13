import io.circe.Error
import io.circe.yaml.parser
import io.circe.yaml.syntax._

import io.circe.generic.auto._
import io.circe.syntax._

import sbt.{Keys, Project}
import sbtrelease.ReleasePlugin.autoImport._

import cats.syntax.either._

import java.io.FileWriter

object Helm {

  val updateChartVersion: ReleaseStep = ReleaseStep(action = st => {
    case class ChartYaml(
                          apiVersion: String,
                          name: String,
                          description: String,
                          `type`: String,
                          version: String,
                          appVersion: String)

    val extracted = Project.extract(st)
    val targetVersion = extracted.get(Keys.version)

    val chartYmlSrc = scala.io.Source.fromFile("helm/poc-conflate/Chart.yaml")
    val chartYaml = parser
      .parse(chartYmlSrc.getLines().mkString("\n"))
      .leftMap(err => err: Error)
      .flatMap(_.as[ChartYaml].map(_.copy(version = targetVersion, appVersion = targetVersion)))
      .valueOr(throw _)

    val fw = new FileWriter("helm/poc-conflate/Chart.yaml", false);
    fw.append(chartYaml.asJson.asYaml.spaces2)
    fw.close()

    st
  })

}
