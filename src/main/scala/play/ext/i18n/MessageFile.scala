package play.ext.i18n

import scala.collection.JavaConversions._

import play.api._
import play.api.i18n.Lang
import play.api.i18n.Messages.UrlMessageSource
import play.utils.Resources

protected[i18n] case class MessageFile(key: String, name: String, loader: MessagesLoader)(implicit configuration: Configuration, env: Environment) {

  import scala.collection.JavaConverters._

  import MessageFile._

  /** resources on classpath matching this filename */
  private def resources = env.classLoader.getResources(joinPaths(messagesPrefix, name)).asScala.filterNot(Resources.isDirectory(env.classLoader, _))

  /** loads messages in this file */
  def load: Map[String, String] = resources.map { resource =>
    log.debug(s"Localization file '$resource'.")
    loader(UrlMessageSource(resource), resource.toString).fold(e => throw e, identity)
  }.fold(Map.empty)(_ ++ _).map {
    case (k, v) =>
      // debug loaded i18n pairs
      log.trace(s"[$key] '$k': '$v'")
      k -> v
  }
}

protected[i18n] object MessageFile {

  /** messages path */
  private def messagesPrefix(implicit configuration: Configuration) = PlayConfig(configuration).get[Option[String]]("play.i18n.path")

  private def fileNames(implicit configuration: Configuration) = configuration.getStringList("play.i18n.files").getOrElse {
    throw new IllegalArgumentException("'play.i18n.files' is missing")
  }.toList

  /** constructs resource path */
  private def joinPaths(prefix: Option[String], second: String) = prefix match {
    case Some(first) => new java.io.File(first, second).getPath
    case None => second
  }

  def apply(languages: Traversable[Lang], format: Format)(implicit configuration: Configuration, env: Environment): Traversable[MessageFile] =
    fileNames.map(name => languages.map(apply(_, format, name))).reduce(_ ++ _)

  def apply(lang: Lang, format: Format, name: String)(implicit configuration: Configuration, env: Environment): MessageFile =
    new MessageFile(lang.code, s"$name${format.toSuffix}.${lang.code}", format.loader)

  def apply(format: Format)(implicit configuration: Configuration, env: Environment): Traversable[MessageFile] =
    fileNames.map { name =>
      Traversable(
        MessageFile("default", s"$name${format.toSuffix}", format.loader),
        MessageFile("default.play", s"$name${format.toSuffix}.default", format.loader)
      )
    }.reduce(_ ++ _)
}
