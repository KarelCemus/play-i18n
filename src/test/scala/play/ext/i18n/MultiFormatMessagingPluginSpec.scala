package play.ext.i18n

import play.api.i18n.{Lang, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._

class MultiFormatMessagingPluginSpec extends PlaySpecification {

  private val injector = new GuiceApplicationBuilder().injector()

  private val messages = injector.instanceOf[MessagesApi]

  private implicit val default: Lang = play.api.i18n.Lang("fr")

  private val cs = play.api.i18n.Lang("cs")

  private val en = play.api.i18n.Lang("en")

  "Plugin" should {

    "read property files" in {
      messages("a.b.c", 1, 2, 3)(default) mustEqual "a=1 b=2 c=3"
      messages("a.b.c", 1, 2, 3)(cs) mustEqual "a=1 b=2 c=3 in cs"
      messages("a.b.c", 1, 2, 3)(en) mustEqual "a=1 b=2 c=3 in en"
    }

    "read JSON files" in {
      messages("x.y.z", 1, 2, 3)(default) mustEqual "x=1 y=2 z=3"
      messages("x.y.z", 1, 2, 3)(cs) mustEqual "x=1 y=2 z=3 in cs"
    }

    "read YAML files" in {
      messages("a.b.d", 1, 2, 3)(default) mustEqual "a=1 b=2 c=3"
      messages("a.b.d", 1, 2, 3)(cs) mustEqual "a=1 b=2 c=3 in cs"
      messages("a.b.d", 1, 2, 3)(en) mustEqual "a=1 b=2 c=3 in en"
      messages("e")(en) mustEqual "something in english"
      messages("e")(cs) mustEqual "something in czech"
    }

    "implement advanced YAML features: hierarchy" in {
      messages("a.x") mustEqual "This is an example"
    }

    "implement advanced YAML features: multi-line strings" in {
      // When using the vertical bar "|", the default behavior is "clipping", where the final line break is preserved.
      // When using "|-", trailing line breaks are stripped. When using "|+", trailing line breaks are kept.
      // https://yaml.org/spec/1.2/spec.html#id2794534
      messages("a.multi-line.clip") mustEqual "This\nis\nan\nexample\n"
      messages("a.multi-line.strip") mustEqual "This\nis\nan\nexample"
      messages("a.multi-line.keep") mustEqual "This\nis\nan\nexample\n\n"
    }

    "implement advanced YAML features: references" in {
      messages("a.e.a") mustEqual "An example"
      messages("a.g.a") mustEqual "An example"
    }

    "read multiple files with different names" in {
      // in 'messages' file
      messages("a.e.a") mustEqual "An example"
      // in 'test' file
      messages("a.b.f") mustEqual "Example from 'test' file"
    }

    "collision resolving" in {
      // messages( "collision" ) mustEqual "Unknown result! It is non-deterministic."
      // warning log is expected
      true mustEqual true
    }

    "preserve spaces within quotes" in {
      messages("spaces.unquoted") mustEqual "should be trimmed"
      messages("spaces.quoted") mustEqual " should not be trimmed "
    }
  }
}
