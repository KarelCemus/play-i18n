# YAML Messaging plugin for Play framework

**Note: This version supports Play framework 2.3.x.**

By default, [Play framework 2](http://playframework.com/) is delivered with default Messaging plugin using property
files. The syntax is not much convenient thus this plugin delivers an alternative version to this plugin supporting
YAML format for messages.


## How to add the plugin into the project

To your SBT `build.sbt` add the following lines:

```scala
libraryDependencies ++= Seq(
  // YAML localization plugin
  "com.github.karelcemus" %% "play-i18n" % "0.1"
)
```

## How to use the this localization plugin

TODO

**Example:**

```scala
TODO

```

## Configuration

TODO

There is already default configuration but it can be overwritten in your `conf/application.conf` file.

| Key                           | Type   | Default                       | Description                         |
|-------------------------------|-------:|------------------------------:|-------------------------------------|
|                               |        |                               |                                     |


## Worth knowing to avoid surprises

The library configuration automatically **disables default Messaging plugin**, it contains the following line in its `conf/reference.conf`.
You do not have to take care of it but it is good to be aware of it, because it **replaces** the EHCache by redis.

```
# disable default Play framework cache plugin
defaultmessagesplugin = disabled
```

The library enables the YAML plugin through `conf/play.plugins` with priority 100, which is also the priority of the default Messaging plugin.
