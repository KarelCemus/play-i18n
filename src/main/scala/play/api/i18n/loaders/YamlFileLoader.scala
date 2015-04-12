package play.api.i18n.loaders

import play.api.PlayException
import play.api.PlayException.ExceptionSource

import play.api.i18n.Messages.MessageSource
import play.api.i18n.MessagesLoader

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

  override def apply(messageSource: MessageSource, messageSourceName: String): Either[ExceptionSource, Map[String, String]] =
    try {
      val yaml = new Yaml(new Constructor(), new Representer(), new DumperOptions(), new CustomResolver())
      val data = yaml.load(messageSource.read)
      Right(flatten(data))
    } catch {
      case exception: ScannerException => Left(new YamlSourceException(exception, messageSourceName))
    }

  private def flatten(data: Any): Map[String, String] = Map.empty
}

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