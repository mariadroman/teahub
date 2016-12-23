name := "teahub"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"
val playVersion = "2.5.4"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(RoutesCompiler)
libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3"
)
routesGenerator := InjectedRoutesGenerator
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
