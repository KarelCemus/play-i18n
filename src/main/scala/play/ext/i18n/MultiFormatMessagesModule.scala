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
class MultiFormatMessagesApiProvider @Inject()(langs: Langs, messageLoadersApi: MessageLoadersApi) extends Provider[MessagesApi] {
  private val api = new DefaultMessagesApi(messages = messageLoadersApi.messages, langs = langs)

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
