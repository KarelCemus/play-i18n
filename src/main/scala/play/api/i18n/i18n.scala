package play.api

package object i18n {

  def missing( key: String ) = throw new UnexpectedException( Some( s"Missing configuration key 'i18n.$key'." ) )
}