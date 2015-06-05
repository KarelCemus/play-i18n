package play.ext.i18n

import play.api.Application
import play.api.i18n.{Lang, DefaultMessagesPlugin}

import play.ext.i18n.MessagesLoaders._

/** Messaging plugin for parsing files in various different formats
  *
  * @author Karel Cemus
  */
class MultiFormatMessagingPlugin(app: Application) extends DefaultMessagesPlugin(app) {

  /**
   * Is this plugin enabled. Default is true
   *
   * {{{
   * i18n.enabled = true
   * }}}
   */
  override def enabled = app.configuration.getBoolean("i18n.enabled").getOrElse(missing("enabled"))

  protected implicit def application: Application = app

  /** loads all messages */
  override protected def messages =
    allFiles.map { file =>
      file.key -> file.load
    }.foldLeft(Map.empty[String, Map[String, String]]) {
      case (merged, (lang, data)) if merged.contains(lang) =>
        // detect collisions and log them
        data.keys.foreach { key =>
          if ( merged( lang ).contains( key ) )
            log.warn( s"Localization key '$key' is defined in multiple files for language '$lang'." )
        }
        merged + (lang -> data.++(merged(lang)))
      case (merged, (key, data)) =>
        merged + (key -> data)
    }

  /** all files regardless the format */
  protected def allFiles = enabledFormats.flatMap(format => MessageFile(format) ++ MessageFile(languages, format))

  /** enabled loaders, disabled are dropped */
  protected def enabledFormats = app.configuration.getConfig("i18n.formats").map { implicit config =>
    supportedFormats.filter(_.isEnabled)
  }.getOrElse(List.empty)

  /** map of supported loaders, mapping format name -> format loader */
  protected def supportedFormats = List(
    Format("properties", None, PropertyFileLoader),
    Format("yaml", Some("yaml"), YamlFileLoader)
  )

  /** all available languages */
  protected def languages = Lang.availables(app)
}
