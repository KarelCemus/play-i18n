package play.api.i18n.loaders

import play.api.PlayException.ExceptionSource
import play.api.i18n.Messages.MessageSource
import play.api.i18n.{Messages, MessagesLoader}

/** Loader of common property files, implementation taken from Play framework core
  *
  * @author Karel Cemus
  */
class PropertyFileLoader extends MessagesLoader {

  override def apply( messageSource: MessageSource, messageSourceName: String ): Either[ ExceptionSource, Map[ String, String ] ] = {
    new Messages.MessagesParser( messageSource, "" ).parse.right.map { messages =>
      messages.map { message => message.key -> message.pattern }.toMap
    }
  }
}
