import sbt.Keys._
import sbt._

name := "play-i18n"

description := "Messaging localization plugin for the Play framework 2"

organization := "com.github.karelcemus"

scalaVersion := "2.13.12"

crossScalaVersions := Seq(scalaVersion.value, "3.3.1")

val playVersion = "2.9.0"

libraryDependencies ++= Seq(
  // play framework cache API
  "com.typesafe.play" %% "play" % playVersion % "provided",
  // YAML parser, Java library
  "org.yaml" % "snakeyaml" % "2.2",
  // test framework
  "org.specs2" %% "specs2-core" % "4.20.3" % "test",
  // test module for play framework
  "com.typesafe.play" %% "play-test" % playVersion % "test",
  "com.typesafe.play" %% "play-specs2" % playVersion % "test"
)

javacOptions ++= Seq("-Xlint:unchecked", "-encoding", "UTF-8")

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Yrangepos")

ThisBuild / versionScheme := Some("semver-spec")

homepage := Some(url("https://github.com/karelcemus/play-i18n"))

licenses := Seq("Apache 2" -> url("https://www.apache.org/licenses/LICENSE-2.0"))

publishMavenStyle := true

pomIncludeRepository := { _ => false }

pomExtra :=
    <scm>
      <url>git@github.com:KarelCemus/play-i18n.git</url>
      <connection>scm:git@github.com:KarelCemus/play-i18n.git</connection>
    </scm>
    <developers>
      <developer>
        <name>Karel Cemus</name>
      </developer>
    </developers>

// Publish settings
publishTo := sonatypePublishToBundle.value

enablePlugins(GitVersioning)
git.gitTagToVersionNumber := { tag: String =>
  if(tag matches "[0-9]+\\..*") Some(tag)
  else None
}
