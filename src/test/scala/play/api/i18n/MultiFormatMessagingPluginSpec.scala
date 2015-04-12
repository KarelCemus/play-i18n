package play.api.i18n

import org.specs2.mutable.Specification
import play.api.test.WithApplication

class MultiFormatMessagingPluginSpec extends Specification {

  val default = play.api.i18n.Lang( "fr" )
  val cs = play.api.i18n.Lang( "cs" )
  val en = play.api.i18n.Lang( "en" )

  "Plugin" should {

    "read property files" in new WithApplication( ) {

      Messages( "a.b.c", 1, 2, 3 )(default) mustEqual "a=1 b=2 c=3"
      Messages( "a.b.c", 1, 2, 3 )(cs) mustEqual "a=1 b=2 c=3 in cs"
      Messages( "a.b.c", 1, 2, 3 )(en) mustEqual "a=1 b=2 c=3 in en"
    }
  }
}
