import sbt._
import sbtrelease.ReleasePlugin.autoImport.ReleaseKeys.versions
import sbtrelease.ReleasePlugin.autoImport.{releaseVcs, releaseVersion, ReleaseStep}
import sbtrelease.Versions

object Changelog {

  val updateChangelog: ReleaseStep = ReleaseStep(action = st => {
    val extracted = Project.extract(st)
    val vcs = extracted.get(releaseVcs).getOrElse(sys.error("Aborting release. Working directory is not a repository of a recognized VCS."))
    vcs.cmd("fetch", "--tags").!!
    val allTags: String = vcs.cmd("tag", "-l", "--sort=v:refname").!!.trim
    val lastTag: String = allTags.split("\n").last
    st.log.info(s"Last Tag: \$lastTag")
    val commitHistory = vcs.cmd("log", "--no-merges", "--format=%s", s"\${lastTag}..HEAD").!!
    val vs: Versions = st.get(versions).getOrElse(sys.error("No versions are set! Was this release part executed before inquireVersions?"))
    writeChangeLog(makeChangeString(vs._1, commitHistory))
    st.log.info(s"Updating changelog")
    vcs.cmd("add", "CHANGELOG.md").!!

    st
  })

  def makeChangeString(version: String, commits: String): String =
    s"## Version: \$version\n\n  - " + commits.split("\n").toList.mkString("\n  - ") + "\n\n ---\n\n"

  def writeChangeLog(change: String): Unit = {
    import java.io.FileWriter
    val source = scala.io.Source.fromFile("CHANGELOG.md")
    val lines = source.getLines()
    val oldLog = if (lines.nonEmpty) lines.reduceLeft(_ + "\n" + _) else ""
    source.close()

    val fw = new FileWriter("CHANGELOG.md", false);
    fw.append(change)
    fw.append(oldLog + "\n\n")
    fw.close()
  }

}

