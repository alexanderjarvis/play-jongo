import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "play-jongo"
    val appVersion      = "0.4"

    val appDependencies = Seq(
      "org.mongodb" % "mongo-java-driver" % "2.11.1",
      "org.jongo" % "jongo" % "0.4",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.1" % "optional"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      organization := "uk.co.panaxiom",
      javacOptions ++= Seq("-source", "1.6", "-target", "1.6", "-encoding", "UTF-8")
      //resolvers += ("cloudbees-jongo-early-release" at "http://repository-jongo.forge.cloudbees.com/release")
    )



}
