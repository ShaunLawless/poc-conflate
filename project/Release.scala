import sbt._
import Github.{dockerRepoBase, gitHubRepo, gitHubToken, githubDockerOwner, packageRepoBase, realm}
import com.typesafe.sbt.SbtNativePackager.Docker
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.{dockerRepository, dockerUsername}
import sbt.Keys.{baseDirectory, credentials, publish, thisProjectRef}
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.Version
import sbtrelease.Version.Bump.Minor

object Release {
  import ReleaseTransformations._

  val releaseBump: Version.Bump = Minor

  val publishWithCredentials: ReleaseStep = ReleaseStep(action = st => {
    val extracted = Project.extract(st)
    st.log.info("Setting credentials for publishing package")
    val newState = extracted.appendWithSession(
      Seq(
        ThisBuild / credentials := Seq(Credentials(realm, packageRepoBase, "_", gitHubToken))
      ),
      st
    )
    val ref = extracted.get(thisProjectRef)
    val newExtracted = Project.extract(newState)
    newExtracted.runAggregated(ref / (Global / releasePublishArtifactsAction), newState)
  })

  val publishDocker: ReleaseStep = ReleaseStep(action = st => {
    st.log.info("Publishing Docker Image again")
    val baseSt: File = st.setting(ThisBuild / baseDirectory)
    val coreRef = ProjectRef(baseSt, "core")
    val updatedState = Project
      .extract(st)
      .appendWithSession(
        Seq(
          coreRef / dockerRepository := s"\$dockerRepoBase/\$githubDockerOwner".some,
          coreRef / dockerUsername := gitHubRepo.some
        ),
        st
      )
    val updatedExtracted = Project.extract(updatedState)
    val base: File = updatedState.setting(ThisBuild / baseDirectory)
    val core = ProjectRef(base, "core")
    val (s, _) = updatedExtracted.runTask(core / Docker / publish, updatedState)
    s
  })

  val releaseSteps: Seq[ReleaseStep] = Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    Helm.updateChartVersion,
    Changelog.updateChangelog,
    commitReleaseVersion,
    tagRelease,
    publishWithCredentials,
    publishDocker,
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
}

