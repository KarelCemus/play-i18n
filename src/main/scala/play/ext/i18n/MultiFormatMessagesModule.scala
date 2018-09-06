package play.ext.i18n

import com.ibm.icu.text.MessageFormat
import javax.inject.{Inject, Singleton}

import collection.JavaConverters._

import play.api.i18n._
import play.api.inject.Module
import play.api.{Configuration, Environment}
import play.ext.i18n.MessagesLoaders._


/** Messaging plugin for parsing files in various different formats
  *
  * @author Karel Cemus
  */
@Singleton
class MultiFormatMessagesApi @Inject()(implicit environment: Environment, configuration: Configuration, langs: Langs) extends DefaultMessagesApi(messages = {

  implicit val config = configuration
  implicit val env    = environment

  /** all files regardless the format */
  def allFiles: List[MessageFile] = enabledFormats.flatMap(format => MessageFile(format) ++ MessageFile(langs.availables, format))

  /** enabled loaders, disabled are dropped */
  def enabledFormats: List[Format] = supportedFormats.filter(_.isEnabled(configuration.get[Configuration]("play.i18n.formats")))

  /** map of supported loaders, mapping format name -> format loader */
  def supportedFormats = List(
    Format("properties", None, PropertyFileLoader),
    Format("yaml", Some("yaml"), YamlFileLoader)
  )

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
}, langs = langs) {

  private val useICUMessageFormat = configuration.get[Boolean]("play.i18n.useICUMessageFormat")

  override def translate(key: String, args: Seq[Any])(implicit lang: Lang): Option[String] = {
    if (useICUMessageFormat) {
      val codesToTry = Seq(lang.code, lang.language, "default", "default.play")
      val pattern: Option[String] =
        codesToTry.foldLeft[Option[String]](None)((res, lang) =>
          res.orElse(messages.get(lang).flatMap(_.get(key))))
      pattern.map(pattern => {
        val mf = new MessageFormat(pattern, lang.toLocale)
        if (args.length == 1 && args(0).isInstanceOf[Map[_, _]]) {
          mf.format(args(0).asInstanceOf[Map[String, Any]].asJava)
        } else {
          mf.format(args.map(_.asInstanceOf[java.lang.Object]).toArray)
        }
      })
    } else {
      super.translate(key, args)
    }
  }
}

class MultiFormatMessagesModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = {
    Seq(
      bind[Langs].toProvider[DefaultLangsProvider],
      bind[MessagesApi].to[MultiFormatMessagesApi],
      bind[play.i18n.MessagesApi].toSelf,
      bind[play.i18n.Langs].toSelf
    )
  }
}