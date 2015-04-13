package play.ext.i18n.loaders

import play.api.PlayException
import play.api.PlayException.ExceptionSource
import play.api.i18n.Messages.MessageSource

import play.ext.i18n.MessagesLoader

import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Representer
import org.yaml.snakeyaml.resolver.Resolver
import org.yaml.snakeyaml.resolver.Resolver._
import org.yaml.snakeyaml.scanner.ScannerException
import org.yaml.snakeyaml.{DumperOptions, Yaml}

/** YAML messages loader, all data it loads as Strings instead of number, dates, etc.
  *
  * @author Karel Cemus
  */
class YamlFileLoader extends MessagesLoader {

  import scala.collection.JavaConverters._

  private type JavaMap = java.util.Map[_, _]

  override def apply(messageSource: MessageSource, messageSourceName: String): Either[ExceptionSource, Map[String, String]] =
    try {
      // YAML document parser
      val yaml = new Yaml(new Constructor(), new Representer(), new DumperOptions(), new CustomResolver())
      // load data as a hierarchical map
      val data = yaml.loadAs(messageSource.read, classOf[JavaMap])
      // flatten the map
      Right(flatten(data))
    } catch {
      case exception: ScannerException =>
        // parsing failed, transform an error
        val error = new YamlSourceException(exception, messageSourceName)
        Left(error)
    }

  private def flatten(data: JavaMap): Map[String, String] = data.asScala.map {
    // inner node
    case (prefix: String, map: JavaMap) => flatten(map).map {
      case (suffix, value) => s"$prefix.$suffix" -> value
    }
    // leaf
    case (key: String, value: String) => Map(key -> value.trim)
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