package play.ext.i18n.loaders

import scala.jdk.CollectionConverters._

import play.api.i18n.Messages.{MessageSource, UrlMessageSource}

import org.specs2.mutable.Specification

class YamlFileLoaderSpecs extends Specification {

  "YAML parser" should {

    val parser = new YamlFileLoader()

    "handle empty message file" in {
      parser(new MessageSource {
        override def read: String = ""
      }, "empty") mustEqual Right(Map.empty)
    }

    "handle non-empty message file" in {
      val resource = new UrlMessageSource(this.getClass.getClassLoader.getResources("messages.yaml.cs").asScala.toList.head)
      parser(resource, "messages.yaml") mustEqual Right(Map("a.b.d" -> "a={0} b={1} c={2} in cs"))
    }
  }
}
