import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "play-jongo"
    val appVersion      = "0.3"

    val appDependencies = Seq(
      "org.mongodb" % "mongo-java-driver" % "2.10.1",
      "org.jongo" % "jongo" % "0.3"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      organization := "uk.co.panaxiom"
      //resolvers += ("cloudbees-jongo-early-release" at "http://repository-jongo.forge.cloudbees.com/release")
    )

    javacOptions ++= Seq("-target", "6")

}
