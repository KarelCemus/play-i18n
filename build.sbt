import com.typesafe.sbt.pgp.PgpKeys
import sbt.Keys._
import sbt._

name := "play-i18n"

description := "Messaging localization plugin for the Play framework 2"

organization := "com.github.karelcemus"

scalaVersion := "2.12.4"

crossScalaVersions := Seq( scalaVersion.value, "2.11.12" )

val playVersion = "2.6.10"

val specs2Version = "4.0.2"

libraryDependencies ++= Seq(
  // play framework cache API
  "com.typesafe.play" %% "play" % playVersion % "provided",
  // YAML parser, Java library
  "org.yaml" % "snakeyaml" % "1.19",
  // ICU4J message format support
  "com.ibm.icu" % "icu4j" % "62.1",
  // test framework
  "org.specs2" %% "specs2-core" % specs2Version % "test",
  // test module for play framework
  "com.typesafe.play" %% "play-test" % playVersion % "test",
  "com.typesafe.play" %% "play-specs2" % playVersion % "test"
)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

javacOptions ++= Seq( "-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-encoding", "UTF-8" )

scalacOptions ++= Seq( "-deprecation", "-feature", "-unchecked", "-Yrangepos" )

homepage := Some( url( "https://github.com/karelcemus/play-i18n" ) )

licenses := Seq( "Apache 2" -> url( "http://www.apache.org/licenses/LICENSE-2.0" ) )

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

// Release plugin settings
releaseCrossBuild := true
releaseTagName := ( version in ThisBuild ).value
releasePublishArtifactsAction := PgpKeys.publishSigned.value

// Publish settings
publishTo := {
  if ( isSnapshot.value ) Some( Opts.resolver.sonatypeSnapshots )
  else Some( Opts.resolver.sonatypeStaging )
}
