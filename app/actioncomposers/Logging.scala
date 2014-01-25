package actioncomposers

import play.api.mvc._
import play.Logger
import scala.concurrent.Future

case class Logging[A](action: Action[A]) extends Action[A] {

  def apply(request: Request[A]): Future[SimpleResult] = {
    Logger.info("Calling action")
    action(request)
  }

  lazy val parser = action.parser
}

