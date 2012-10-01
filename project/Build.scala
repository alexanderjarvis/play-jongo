import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "play-jongo"
    val appVersion      = "0.3-early"

    val appDependencies = Seq(
      "org.mongodb" % "mongo-java-driver" % "2.8.0",
      "org.jongo" % "jongo" % "0.3-early-20120924-1510"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      organization := "uk.co.panaxiom",
      resolvers += ("cloudbees-jongo-early-release" at "http://repository-jongo.forge.cloudbees.com/release")
    )

}
