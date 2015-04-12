<h1 align="center">Multi-format Messaging plugin<br/> for Play framework</h1>

**Note: This version supports Play framework 2.3.x. Compatibility with previous versions is not guaranteed.**

[Play framework 2](http://playframework.com/) is delivered with default Messaging plugin using property
files. The syntax is not much convenient as involves a lot of repetition, thus this plugin delivers
an alternative to supporting YAML format for messages including all features of the language.

[![Build Status](http://jenkins.karelcemus.cz/buildStatus/icon?job=play-i18n)](http://jenkins.karelcemus.cz/job/play-i18n)

## How to add the plugin into the project

To your SBT `build.sbt` add the following lines:

```scala
libraryDependencies ++= Seq(
  // YAML localization plugin
  "com.github.karelcemus" %% "play-i18n" % "0.1"
)
```

## How to use the this localization plugin

The end-user API remains same. The plugin is smoothly connected to the current `MessagesAPI` which delivers `Messages`
as a single access point.

**Example:**

```scala
Messages( "my.simple.key", "With", 3, "parameters" )

```

The plugin supports multiple message file formats. Right now, it supports the property files
for backward compatibility and YAML files for their convenient syntax. The base file name remains same as is defined
by Play framework: `messages[.lang]` where lang is optional for a particular language mutation. For YAML files the name
pattern is very similar: `messages.yaml[.lang]` again with the optional language. All these files are looked up at the
classpath.

**Example of property file:**
```properties
my.simple.key = The key resolves to {0} {1} {2}
my.simple.value = Some text
my.another.key = Another localization key
```

**Example of YAML file:**
```yaml
my:
  simple:
    key: The key resolves to {0} {1} {2}
    value: Some text
  another.key: = Another localization key
```

The YAML file format brings some beautiful features such as:

- key hierarchy
- multi-line strings
- sub-tree replacement (naming one tree and referencing it elsewhere)

For full list of features see [Wikipedia](http://en.wikipedia.org/wiki/YAML#Examples)

**Note:**
Although YAML supports data types, the plugin ignores them to preserve the compatibility with the MessagingAPI. 

## Configuration

There is already default configuration but it can be overwritten in your `conf/application.conf` file.

| Key                           | Type   | Default                       | Description                         |
|-------------------------------|-------:|------------------------------:|-------------------------------------|
| messages.path                 | String | empty                         | Prefix of messages files            |
| defaultmessagesplugin         | String | "disabled"                    | Disables default messaging plugin   |
| i18n.enabled                  | Boolean| true                          | Enables this plugin                 |
| i18n.formats.properties       | Boolean| true                          | Enables the format                  |
| i18n.formats.yaml             | Boolean| true                          | Enables the format                  |


## Worth knowing to avoid surprises

The library configuration automatically **disables default Messaging plugin**, it contains the following line in its `conf/reference.conf`.
You do not have to take care of it but it is good to be aware of it, because it **replaces** the DefaultMessagingPlugin.

```
# disable default Play framework cache plugin
defaultmessagesplugin = disabled
```

The library automatically enables the YAML plugin through `conf/play.plugins` with priority 100, which is also the priority of the default Messaging plugin.
