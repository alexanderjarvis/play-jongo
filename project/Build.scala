import sbt._
import Keys._
import PlayProject._
import de.johoop.findbugs4sbt.FindBugs._

object ApplicationBuild extends Build {

    val appName         = "play-jongo"
    val appVersion      = "0.5.0-jongo0.4"

    val appDependencies = Seq(
      "org.mongodb" % "mongo-java-driver" % "2.11.1",
      "org.jongo" % "jongo" % "0.4",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.1" % "optional"
    )

    val main = play.Project(appName, appVersion, appDependencies,
      settings = Defaults.defaultSettings ++ findbugsSettings).settings(
      organization := "uk.co.panaxiom",
      description := "Play 2.1 Module for Jongo http://jongo.org/",
      javacOptions ++= Seq("-source", "1.6", "-target", "1.6", "-encoding", "UTF-8"),
      // Maven publishing info
      publishMavenStyle := true,
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (appVersion.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      },
      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false },
      pomExtra := extraPom
    )

    def extraPom = (
      <url>https://github.com/alexanderjarvis/play-jongo</url>
      <licenses>
        <license>
          <name>Apache 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:alexanderjarvis/play-jongo.git</url>
        <connection>scm:git:git@github.com:alexanderjarvis/play-jongo.git</connection>
      </scm>
      <developers>
        <developer>
          <id>alexjarvis</id>
          <name>Alexander Jarvis</name>
          <url>https://github.com/alexanderjarvis</url>
        </developer>
        <developer>
          <id>martin.grotzke</id>
          <name>Martin Grotzke</name>
          <url>https://github.com/magro</url>
        </developer>
      </developers>)

}
