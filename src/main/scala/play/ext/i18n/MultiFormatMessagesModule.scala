package play.ext.i18n

import javax.inject.{Inject, Provider, Singleton}
import play.api.i18n._
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import play.ext.i18n.MessagesLoaders._

/** Messaging plugin for parsing files in various different formats
 *
 * @author Karel Cemus
 */
@Singleton
class MultiFormatMessagesApiProvider @Inject()(implicit environment: Environment, configuration: Configuration, langs: Langs) extends Provider[MessagesApi] {

  /** all files regardless the format */
  private def allFiles: List[MessageFile] = enabledFormats.flatMap(format => MessageFile(format) ++ MessageFile(langs.availables, format))

  /** enabled loaders, disabled are dropped */
  private def enabledFormats: List[Format] = supportedFormats.filter(_.isEnabled(configuration.get[Configuration]("play.i18n.formats")))

  /** map of supported loaders, mapping format name -> format loader */
  private def supportedFormats = List(
    Format("properties", None, PropertyFileLoader),
    Format("yaml", Some("yaml"), YamlFileLoader)
  )

  private def messages: Map[String, Map[String, String]] = allFiles.map { file =>
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

  private val api = new DefaultMessagesApi(messages = messages, langs = langs)

  override def get: MessagesApi = api
}

class MultiFormatMessagesModule extends Module {
  def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[Langs].toProvider[DefaultLangsProvider],
      bind[MessagesApi].toProvider[MultiFormatMessagesApiProvider],
      bind[play.i18n.MessagesApi].toSelf,
      bind[play.i18n.Langs].toSelf
    )
  }
}
