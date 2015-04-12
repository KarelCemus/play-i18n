package play.api.i18n

import play.api.Application
import play.api.i18n.Messages.UrlMessageSource
import play.utils.Resources

protected[ i18n ] case class MessageFile( key: String, name: String, loader: MessagesLoader )( implicit app: Application ) {

  import MessageFile._

  import scala.collection.JavaConverters._

  /** resources on classpath matching this filename */
  private def resources = app.classloader.getResources( joinPaths( messagesPrefix, name ) ).asScala.filterNot( Resources.isDirectory )

  /** loads messages in this file */
  def load: Map[ String, String ] = resources.map { resource =>
    loader( UrlMessageSource( resource ), resource.toString ).fold( e => throw e, identity )
  }.foldLeft( Map.empty[ String, String ] )( _ ++ _ )
}

protected[ i18n ] object MessageFile {

  /** messages path */
  def messagesPrefix( implicit app: Application ) = app.configuration.getString( "messages.path" )

  /** constructs resource path */
  def joinPaths( prefix: Option[ String ], second: String ) = prefix match {
    case Some( first ) => new java.io.File( first, second ).getPath
    case None => second
  }

  def apply( languages: Traversable[ Lang ], format: Format )( implicit app: Application ): Traversable[ MessageFile ] = languages.map( apply( _, format ) )

  def apply( lang: Lang, format: Format )( implicit app: Application ): MessageFile =
    new MessageFile( lang.code, s"messages${format.toSuffix}.${lang.code}", format.loader )

  def apply( format: Format )( implicit app: Application ): Traversable[ MessageFile ] = Traversable(
    MessageFile( "default", s"messages${format.toSuffix}", format.loader ),
    MessageFile( "default.play", s"messages${format.toSuffix}.default", format.loader )
  )
}
