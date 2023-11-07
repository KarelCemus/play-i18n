import sbt.Keys._
import sbt._

name := "play-i18n"

description := "Messaging localization plugin for the Play framework 2"

organization := "com.github.karelcemus"

scalaVersion := "2.13.12"

crossScalaVersions := Seq(scalaVersion.value, "3.3.1")

val playVersion = "3.0.0"

libraryDependencies ++= Seq(
  // play framework cache API
  "org.playframework" %% "play" % playVersion % "provided",
  // YAML parser, Java library
  "org.yaml" % "snakeyaml" % "2.2",
  // test framework
  "org.specs2" %% "specs2-core" % "4.20.3" % "test",
  // test module for play framework
  "org.playframework" %% "play-test" % playVersion % "test",
  "org.playframework" %% "play-specs2" % playVersion % "test"
)

javacOptions ++= Seq("-Xlint:unchecked", "-encoding", "UTF-8")

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Yrangepos")

ThisBuild / versionScheme := Some("semver-spec")

homepage := Some(url("https://github.com/karelcemus/play-i18n"))

licenses := Seq("Apache 2" -> url("https://www.apache.org/licenses/LICENSE-2.0"))

publishMavenStyle := true

pomIncludeRepository := { _ => false }

scmInfo := Some(
  ScmInfo(
    url("https://github.com/KarelCemus/play-i18n.git"),
    "scm:git@github.com:KarelCemus/play-i18n.git"
  )
)
developers := List(
  Developer(id="karel.cemus", name="Karel Cemus", email="", url=url("https://github.com/KarelCemus/"))
)

// Publish settings
publishTo := sonatypePublishToBundle.value

enablePlugins(GitVersioning)
git.gitTagToVersionNumber := { tag: String =>
  if(tag matches "[0-9]+\\..*") Some(tag)
  else None
}
