package play.ext.i18n

import play.api.Configuration

/** supported format, suffix represents file suffix */
protected[i18n] case class Format(name: String, suffix: Option[String], loader: MessagesLoader) {

  def isEnabled(implicit config: Configuration) = config.get[Boolean](name)

  def toSuffix = suffix.map("." + _).getOrElse("")
}
