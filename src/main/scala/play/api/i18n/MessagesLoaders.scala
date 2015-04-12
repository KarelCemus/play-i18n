package play.api.i18n

import play.api.PlayException

import play.api.i18n.Messages.MessageSource
import play.api.i18n.loaders._

trait MessagesLoader {

  def apply(messageSource: MessageSource, messageSourceName: String): Either[PlayException.ExceptionSource, Map[String, String]]
}

trait MessagesLoaders {

  object PropertyFileLoader extends PropertyFileLoader


}

object MessagesLoaders extends MessagesLoaders
