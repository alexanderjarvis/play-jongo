import play.Project._
import de.johoop.findbugs4sbt.FindBugs._

name := "play-jongo"

organization := "uk.co.panaxiom"

description := "Play 2.2 Module for Jongo http://jongo.org/"

version := "0.5.0-jongo0.4"


libraryDependencies ++= Seq(
  "org.mongodb" % "mongo-java-driver" % "2.11.1",
  "org.jongo" % "jongo" % "0.4",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.3" % "optional"
)

playScalaSettings

findbugsSettings

javacOptions ++= Seq("-source", "1.6", "-target", "1.6", "-encoding", "UTF-8")

// Maven publishing info
publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
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
