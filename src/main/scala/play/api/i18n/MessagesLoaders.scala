package play.api.i18n

import play.api.PlayException
import play.api.PlayException.ExceptionSource
import play.api.i18n.Messages.MessageSource

trait MessagesLoader {

  def apply( messageSource: MessageSource, messageSourceName: String ): Either[ PlayException.ExceptionSource, Map[ String, String ] ]
}

trait MessagesLoaders {

  object PropertyFileLoader extends MessagesLoader {

    override def apply( messageSource: MessageSource, messageSourceName: String ): Either[ ExceptionSource, Map[ String, String ] ] = {
      new Messages.MessagesParser( messageSource, "" ).parse.right.map { messages =>
        messages.map { message => message.key -> message.pattern }.toMap
      }
    }
  }

}

object MessagesLoaders extends MessagesLoaders
