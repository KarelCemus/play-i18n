import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin._
import com.typesafe.sbt.pgp.PgpKeys

name := "play-i18n"

description := "Messaging localization plugin for the Play framework 2"

organization := "com.github.karelcemus"

scalaVersion := "2.11.6"

crossScalaVersions := Seq( scalaVersion.value, "2.10.4" )

libraryDependencies ++= Seq(
  // play framework cache API
  "com.typesafe.play" %% "play-cache" % "2.3.8" % "provided",
  // YAML parser, Java library
  "org.yaml" % "snakeyaml" % "1.15",
  // test framework
  "org.specs2" %% "specs2-core" % "3.4" % "test",
  // test module for play framework
  "com.typesafe.play" %% "play-test" % "2.3.8" % "test"
)

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)

javacOptions ++= Seq( "-source", "1.6", "-target", "1.6", "-Xlint:unchecked", "-encoding", "UTF-8" )

scalacOptions ++= Seq( "-deprecation", "-feature", "-unchecked", "-Yrangepos" )

homepage := Some( url( "https://github.com/karelcemus/play-i18n" ) )

licenses := Seq( "Apache 2" -> url( "http://www.apache.org/licenses/LICENSE-2.0" ) )

publishMavenStyle := true

pomIncludeRepository := { _ => false}

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

// Release settings
releaseSettings

ReleaseKeys.crossBuild := true

ReleaseKeys.tagName := ( version in ThisBuild ).value

ReleaseKeys.publishArtifactsAction := PgpKeys.publishSigned.value

// Publish settings
publishTo := {
  if (isSnapshot.value) Some(Opts.resolver.sonatypeSnapshots)
  else Some( Opts.resolver.sonatypeStaging )
}