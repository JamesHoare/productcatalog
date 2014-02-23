
import akka.actor.Props
import com.google.inject.Guice
import com.typesafe.config.ConfigFactory
import java.io.File
import play.api._
import play.api.mvc._
import play.libs.Akka


// Note: this is in the default package.
object Global extends GlobalSettings {

  private lazy val injector = {
    Play.isProd match {
      case true => Guice.createInjector(new ProdModule)
      case false => Guice.createInjector(new DevModule)
    }
  }

  override def getControllerInstance[A](clazz: Class[A]) = {
    injector.getInstance(clazz)
  }

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    println("executed before every request:" + request.toString)
    super.onRouteRequest(request)
  }


  override def onStart(app: Application) {
    //val myActor = Akka.system.actorOf(Props[MyActor], name = "myactor")
    //do something interesting

  }

  override def doFilter(next: EssentialAction): EssentialAction = {
    Filters(super.doFilter(next), LoggingFilter)
  }

  /**
   *
   * Get a file within the .playapp folder in the user's home directory
   *
   * This just adds it to the application’s config, overriding the default values. If the config file doesn’t exist,
   * it adds an empty config. Since Play’s Configuration wraps the Typesafe Config library,
   * we use ConfigFactory to load the config, then wrap that in a Configuration.
   *
   * You can now read the new config values as usual in your application code:
   *
   * def coolFeatureEnabled = current.configuration.getBoolean("coolFeatureEnabled").getOrElse(false)
   *
   * @param filename
   * @return
   */
  def getUserFile(filename: String): File =
    new File(Seq(System.getProperty("user.home"), ".playapp", filename).mkString(File.separator))

  override def onLoadConfig(config: Configuration, path: File, classloader: ClassLoader, mode: Mode.Mode) = {
    val localConfig = Configuration(ConfigFactory.parseFile(getUserFile("local.conf")))
    super.onLoadConfig(config ++ localConfig, path, classloader, mode)
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
      /*val action = requestHeader.tags(Routes.ROUTE_CONTROLLER) +
        "." + requestHeader.tags(Routes.ROUTE_ACTION_METHOD)*/
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