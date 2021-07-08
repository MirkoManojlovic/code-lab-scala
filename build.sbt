name := """code-lab-scala"""
organization := "com.codelab"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"
libraryDependencies += "io.scalaland" %% "chimney" % "0.6.1"

//addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.codelab.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.codelab.binders._"
