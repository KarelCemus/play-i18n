package play.ext.i18n

import play.api.PlayException
import play.api.i18n.Messages.MessageSource

import play.ext.i18n.loaders.{PropertyFileLoader, YamlFileLoader}

trait MessagesLoader {

  def apply(messageSource: MessageSource, messageSourceName: String): Either[PlayException.ExceptionSource, Map[String, String]]
}

trait MessageLoadersApi {
  def allFiles: List[MessageFile]
  def enabledFormats: List[Format]
  def supportedFormats: List[Format]
  def messages: Map[String, Map[String, String]]
}

trait MessagesLoaders {

  object PropertyFileLoader extends PropertyFileLoader

  object YamlFileLoader extends YamlFileLoader

}

object MessagesLoaders extends MessagesLoaders
