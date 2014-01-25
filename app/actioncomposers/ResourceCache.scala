package actioncomposers

import play.api.mvc.{SimpleResult, Request, BodyParser, Action}
import scala.concurrent.Future


/**
 * Created by jameshoare on 25/01/2014.
 */
case class ResourceCache[A](action: Action[A]) extends Action[A] {

  def apply(request: Request[A]): Future[SimpleResult] = {
    //do some caching here
    action(request)
  }

  lazy val parser = action.parser



}
