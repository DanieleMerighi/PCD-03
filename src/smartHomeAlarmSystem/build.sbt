ThisBuild / version := "1.0"

ThisBuild / scalaVersion := "3.8.4"

ThisBuild / libraryDependencies += "org.apache.pekko" %% "pekko-actor-typed" % "1.6.0"

Compile / scalaSource := baseDirectory.value

lazy val root = (project in file("."))
  .settings(
    name := "smartHomeAlarmSystem"
  )
