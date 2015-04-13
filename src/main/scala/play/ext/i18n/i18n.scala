package play.ext

import play.api.{Logger, UnexpectedException}

package object i18n {

  val log = Logger("play.api.i18n")

  def missing(key: String) = throw new UnexpectedException(Some(s"Missing configuration key 'i18n.$key'."))
}