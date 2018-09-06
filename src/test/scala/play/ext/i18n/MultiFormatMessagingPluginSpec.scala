package play.ext.i18n

import play.api.i18n.MessagesApi
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._

class MultiFormatMessagingPluginSpec extends PlaySpecification {

  val injector = new GuiceApplicationBuilder().injector

  val messages = injector.instanceOf[MessagesApi]

  implicit val default = play.api.i18n.Lang("fr")

  val cs = play.api.i18n.Lang("cs")

  val en = play.api.i18n.Lang("en")

  "Plugin" should {

    "read property files" in {
      messages("a.b.c", 1, 2, 3)(default) mustEqual "a=1 b=2 c=3"
      messages("a.b.c", 1, 2, 3)(cs) mustEqual "a=1 b=2 c=3 in cs"
      messages("a.b.c", 1, 2, 3)(en) mustEqual "a=1 b=2 c=3 in en"
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
      messages("a.f") mustEqual "This\nis\nan\nexample"
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

    "properly parse ICU message format in property and YAML files" in {
      val icuInjector = new GuiceApplicationBuilder().configure(Map("play.i18n.useICUMessageFormat" -> true)).build().injector
      val icuMessages = icuInjector.instanceOf[MessagesApi]
      val date = new java.util.Date(0)

      icuMessages("p.icu", Map("gender" -> "female", "num_guests" -> 2, "host" -> "TheHost", "guest" -> "TheGuest", "d" -> date))(en) mustEqual "TheHost invites TheGuest and one other person to her party on Thursday, January 1, 1970."
      icuMessages("p.icu", Map("gender" -> "male", "num_guests" -> 3, "host" -> "TheHost", "guest" -> "TheGuest", "d" -> date))(en) mustEqual "TheHost invites TheGuest and 2 other people to his party on Thursday, January 1, 1970."
      icuMessages("y.icu", Map("gender" -> "female", "num_guests" -> 2, "host" -> "TheHost", "guest" -> "TheGuest", "d" -> date))(en) mustEqual "TheHost invites TheGuest and one other person to her party on Thursday, January 1, 1970."
      icuMessages("y.icu", Map("gender" -> "male", "num_guests" -> 3, "host" -> "TheHost", "guest" -> "TheGuest", "d" -> date))(en) mustEqual "TheHost invites TheGuest and 2 other people to his party on Thursday, January 1, 1970."
    }
  }
}
