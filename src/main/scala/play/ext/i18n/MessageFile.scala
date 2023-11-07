package play.ext.i18n

import play.api._

protected[i18n] case class MessageFile(key: String, name: String, loader: MessagesLoader)(implicit configuration: Configuration, env: Environment) {

  import scala.jdk.CollectionConverters._

  import play.api.i18n.Messages.UrlMessageSource
  import play.utils.Resources

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

  import play.api.i18n.Lang

  /** messages path */
  private def messagesPrefix(implicit configuration: Configuration) =
    if (configuration.has("play.i18n.path")) configuration.getOptional[String]("play.i18n.path") else None

  private def fileNames(implicit configuration: Configuration) = configuration.get[Seq[String]]( "play.i18n.files")

  /** constructs resource path */
  private def joinPaths(prefix: Option[String], second: String) = prefix match {
    case Some(first) => new java.io.File(first, second).getPath
    case None => second
  }

  def apply(languages: Iterable[Lang], format: Format)(implicit configuration: Configuration, env: Environment): Iterable[MessageFile] =
    fileNames.map(name => languages.flatMap(apply(_, format, name))).reduce(_ ++ _)

  def apply(lang: Lang, format: Format, name: String)(implicit configuration: Configuration, env: Environment): Iterable[MessageFile] = {
    Set(s"$name${format.toSuffix}.${lang.code}", s"$name.${lang.code}${format.toSuffix}").map(
      new MessageFile(lang.code, _, format.loader)
    )
  }

  def apply(format: Format)(implicit configuration: Configuration, env: Environment): Iterable[MessageFile] =
    fileNames.map { name =>
      Iterable(
        MessageFile("default", s"$name${format.toSuffix}", format.loader),
        MessageFile("default.play", s"$name${format.toSuffix}.default", format.loader)
      )
    }.reduce(_ ++ _)
}
