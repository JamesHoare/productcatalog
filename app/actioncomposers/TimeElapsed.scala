package actioncomposers

import play.api.mvc.{SimpleResult, Request, Action}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

case class TimeElapsed[A](action: Action[A]) extends Action[A] {
  def apply(request: Request[A]): Future[SimpleResult] = {
    val start = System.currentTimeMillis
    action(request).map {
      res =>
        val totalTime = System.currentTimeMillis - start
        println("Elapsed time: %1d ms".format(totalTime))
        res
    }
  }

  lazy val parser = action.parser
}
