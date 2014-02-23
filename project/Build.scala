import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "productcatalog"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    cache,
    jdbc,
    "mysql" % "mysql-connector-java" % "5.1.18",
    "com.typesafe.play" %% "play-slick" % "0.5.0.8",
    "org.webjars" %% "webjars-play" % "2.2.0",
    "org.webjars" % "bootstrap" % "2.3.1",
    "com.google.inject" % "guice" % "3.0",
    "com.tzavellas" % "sse-guice" % "0.7.1"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
