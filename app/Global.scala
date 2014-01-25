
import play.api._
import play.api.mvc._

// Note: this is in the default package.
object Global extends GlobalSettings {

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    println("executed before every request:" + request.toString)
    super.onRouteRequest(request)
  }


  override def onStart(app: Application) {
    //do something interesting

  }

  override def doFilter(next: EssentialAction): EssentialAction = {
    Filters(super.doFilter(next), LoggingFilter)
  }
}

/**
 * Global Filter which has full access to request
 */

import play.api.Logger
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object LoggingFilter extends EssentialFilter {
  def apply(nextFilter: EssentialAction) = new EssentialAction {
    def apply(requestHeader: RequestHeader) = {
      val action = requestHeader.tags(Routes.ROUTE_CONTROLLER) +
        "." + requestHeader.tags(Routes.ROUTE_ACTION_METHOD)
      val startTime = System.currentTimeMillis
      nextFilter(requestHeader).map {
        result =>
          val endTime = System.currentTimeMillis
          val requestTime = endTime - startTime
          Logger.info(s"${requestHeader.method} ${requestHeader.uri}" +
            s" took ${requestTime}ms and returned ${result.header.status}")
          result.withHeaders("Request-Time" -> requestTime.toString)
      }
    }
  }
}