import _root_.controllers.Products
import play.api._
import play.api.mvc._

// Note: this is in the default package.
object Global extends GlobalSettings {

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    println("executed before every request:" + request.toString)
    super.onRouteRequest(request)
  }


  override def onStart(app: Application) {
    Logger.info("Invoking Google Geo Code")
    val valuesFetchedForLatitudeAndLongitude = Products.fetchLatitudeAndLongitude("London")
    println(valuesFetchedForLatitudeAndLongitude)
  }

}