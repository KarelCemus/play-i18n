package play.api.i18n

import play.api.Application
import play.api.i18n.MessagesLoaders._

/** Messaging plugin for parsing files in various different formats
  *
  * @author Karel Cemus
  */
class MultiFormatMessagingPlugin( app: Application ) extends DefaultMessagesPlugin( app ) {

  protected implicit def application: Application = app

  /** map of supported loaders, mapping format name -> format loader */
  protected def supportedFormats = List(
    Format( "properties", None, PropertyFileLoader )
  )

  /** enabled loaders, disabled are dropped */
  protected def enabledFormats = app.configuration.getConfig( "i18n.formats" ).map { implicit config =>
    supportedFormats.filter( _.isEnabled )
  }.getOrElse( List.empty )

  /** all available languages */
  protected def languages = Lang.availables( app )

  /** all files regardless the format */
  protected def allFiles = enabledFormats.flatMap( format => MessageFile( format ) ++ MessageFile( languages, format ) )

  /** loads all messages */
  override protected def messages =
    allFiles.map { file =>
      file.key -> file.load
    }.foldLeft( Map.empty[ String, Map[ String, String ] ] ) {
      case (merged, (key, data)) if merged.contains( key ) =>
        merged + ( key -> data.++( merged( key ) ) )
      case (merged, (key, data)) =>
        merged + ( key -> data )
    }

  /**
   * Is this plugin enabled. Default is true
   *
   * {{{
   * i18n.enabled = true
   * }}}
   */
  override def enabled = app.configuration.getBoolean( "i18n.enabled" ).getOrElse( missing( "enabled" ) )
}
