import de.johoop.findbugs4sbt.FindBugs._

name := "play-jongo"

organization := "uk.co.panaxiom"

description := "Play 2.6.x Module for Jongo http://jongo.org/"

version := "2.1.0-jongo1.3"

scalaVersion := "2.12.5"

libraryDependencies ++= Seq(
  "org.mongodb" % "mongo-java-driver" % "3.4.2",
  "org.jongo" % "jongo" % "1.3.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.9" % "optional",
  "org.easytesting" % "fest-assert" % "1.4" % "test"
)

lazy val root = (project in file(".")).enablePlugins(PlayJava)

findbugsSettings

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8")

// "-v" needed for more verbose output, otherwise only the number of tests is reported
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-v"))

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
