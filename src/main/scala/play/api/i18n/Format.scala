package play.api.i18n

import play.api.Configuration

/** supported format, suffix represents file suffix */
protected[ i18n ] case class Format( name: String, suffix: Option[ String ], loader: MessagesLoader ) {

  def isEnabled( implicit config: Configuration ) = config.getBoolean( name ).getOrElse( missing( s"formats.$name" ) )

  def toSuffix = suffix.map( "." + _ ).getOrElse( "" )
}
