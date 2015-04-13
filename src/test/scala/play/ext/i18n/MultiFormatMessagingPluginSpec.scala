package play.ext.i18n

import play.api.Play
import play.api.i18n.Messages
import play.api.test._

import org.specs2.mutable.Specification
import org.specs2.specification._

class MultiFormatMessagingPluginSpec extends Specification with BeforeAfterAll with Scope {

  override def beforeAll( ): Unit = Play.start( FakeApplication( ) )

  override def afterAll( ): Unit = Play.stop( )

  implicit val default = play.api.i18n.Lang( "fr" )

  val cs = play.api.i18n.Lang( "cs" )

  val en = play.api.i18n.Lang( "en" )

  "Plugin" should {

    "read property files" in {
      Messages( "a.b.c", 1, 2, 3 )( default ) mustEqual "a=1 b=2 c=3"
      Messages( "a.b.c", 1, 2, 3 )( cs ) mustEqual "a=1 b=2 c=3 in cs"
      Messages( "a.b.c", 1, 2, 3 )( en ) mustEqual "a=1 b=2 c=3 in en"
    }

    "read YAML files" in {
      Messages( "a.b.d", 1, 2, 3 )( default ) mustEqual "a=1 b=2 c=3"
      Messages( "a.b.d", 1, 2, 3 )( cs ) mustEqual "a=1 b=2 c=3 in cs"
      Messages( "a.b.d", 1, 2, 3 )( en ) mustEqual "a=1 b=2 c=3 in en"
    }

    "implement advanced YAML features: hierarchy" in {
      Messages( "a.x" ) mustEqual "This is an example"
    }

    "implement advanced YAML features: multi-line strings" in {
      Messages( "a.f" ) mustEqual "This\nis\nan\nexample"
    }

    "implement advanced YAML features: references" in {
      Messages( "a.e.a" ) mustEqual "An example"
      Messages( "a.g.a" ) mustEqual "An example"
    }
  }
}
