// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe models.repository
resolvers += "Typesafe models.repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-atmos-play" % "0.3.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.6.4")


