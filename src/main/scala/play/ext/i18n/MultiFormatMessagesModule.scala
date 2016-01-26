package play.ext.i18n

import javax.inject.Inject

import play.api.i18n._
import play.api.inject.Module
import play.api.{Configuration, Environment}

import play.ext.i18n.MessagesLoaders._

/** Messaging plugin for parsing files in various different formats
  *
  * @author Karel Cemus
  */
class MultiFormatMessagesApi @Inject()(implicit environment: Environment, configuration: Configuration, langs: Langs) extends DefaultMessagesApi(environment, configuration, langs) {

  /** loads all messages */
  override val messages =
    allFiles.map { file =>
      file.key -> file.load
    }.foldLeft(Map.empty[String, Map[String, String]]) {
      case (merged, (lang, data)) if merged.contains(lang) =>
        // detect collisions and log them
        data.keys.foreach { key =>
          if (merged(lang).contains(key))
            log.warn(s"Localization key '$key' is defined in multiple files for language '$lang'.")
        }
        merged + (lang -> data.++(merged(lang)))
      case (merged, (key, data)) =>
        merged + (key -> data)
    }

  /** all files regardless the format */
  protected def allFiles = enabledFormats.flatMap(format => MessageFile(format) ++ MessageFile(langs.availables, format))

  /** enabled loaders, disabled are dropped */
  protected def enabledFormats = configuration.getConfig("play.i18n.formats").map { implicit config =>
    supportedFormats.filter(_.isEnabled(config))
  }.getOrElse(List.empty)

  /** map of supported loaders, mapping format name -> format loader */
  protected def supportedFormats = List(
    Format("properties", None, PropertyFileLoader),
    Format("yaml", Some("yaml"), YamlFileLoader)
  )
}

class MultiFormatMessagesModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = {
    Seq(
      bind[Langs].to[DefaultLangs],
      bind[MessagesApi].to[MultiFormatMessagesApi]
    )
  }
}