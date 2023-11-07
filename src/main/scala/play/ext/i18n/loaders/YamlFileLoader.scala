package play.ext.i18n.loaders

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.resolver.Resolver
import org.yaml.snakeyaml.resolver.Resolver._
import org.yaml.snakeyaml.scanner.ScannerException
import play.api.PlayException
import play.api.PlayException.ExceptionSource
import play.api.i18n.Messages.MessageSource
import play.ext.i18n.MessagesLoader

/** YAML messages loader, all data it loads as Strings instead of number, dates, etc.
  *
  * @author Karel Cemus
  */
class YamlFileLoader extends MessagesLoader {

  import scala.jdk.CollectionConverters._

  private type JavaMap = java.util.Map[_, _]

  override def apply(messageSource: MessageSource, messageSourceName: String): Either[ExceptionSource, Map[String, String]] =
    try {
      // YAML document parser
      val yaml = new Yaml()
      // load data as a hierarchical map
      val data: Option[JavaMap] = Option(yaml.loadAs(messageSource.read, classOf[JavaMap]))
      val map = data.map(flatten).getOrElse(Map.empty[String, String])
      Right(map)
    } catch {
      case exception: ScannerException =>
        // parsing failed, transform an error
        val error = new YamlSourceException(exception, messageSourceName)
        Left(error)
    }

  private def flatten(data: JavaMap): Map[String, String] = data.asScala.collect {
    // inner node
    case (prefix: String, map: JavaMap) => flatten(map).map {
      case (suffix, value) => s"$prefix.$suffix" -> value
    }
    // leaf
    case (key: String, value: String) => Map(key -> value)
  }.fold(Map.empty)(_ ++ _)
}

/** Custom resolved disables parsing of numbers, dates and other types so it produces strings only */
private class CustomResolver extends Resolver {

  override protected def addImplicitResolvers(): Unit = {
    // resolve everything as a string
    addImplicitResolver(Tag.MERGE, MERGE, "<")
    addImplicitResolver(Tag.NULL, NULL, "~nN\u0000")
    addImplicitResolver(Tag.NULL, EMPTY, null)
  }
}

private class YamlSourceException(exception: ScannerException, source: String) extends PlayException.ExceptionSource(
  s"Parsing of YAML file '$source' failed.", exception.toString, exception) {

  override def sourceName(): String = source

  override def input(): String = exception.getProblem

  override def position(): Integer = exception.getProblemMark.getColumn

  override def line(): Integer = exception.getProblemMark.getLine
}