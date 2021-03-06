package controllers

import models.Product
import play.api.libs.json._
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api.mvc._
import play.api.Play.current
import java.net.URLEncoder
import play.api.libs.ws.{Response, WS}
import akka.util.Timeout
import scala.concurrent.{Future, TimeoutException, Await}
import scala.concurrent.duration._
import scala.Option
import actioncomposers._
import play.api.libs.concurrent._
import Actors.ProductsActor
import akka.actor.Props
import akka.pattern.ask


/**
 * In Play, each controller is a Scala object that defines one or more actions.
 * Play uses an object instead of a class because the controller doesn’t have any state;
 * this controller is used to group product actions.
 *
 *
 * DON’T DEFINE A var IN A CONTROLLER OBJECT
 *
 */
object Products extends Controller {


  case class FetchProducts(resourceType: String, channelId: String)


  def list = Action {

    implicit request =>

      Ok(Json.toJson(Product.findAll))
  }


  def details(ean: Long) = Action {
    implicit request =>

      Product.findByEan(ean).map {
        product =>
          Ok("")
      }.getOrElse(NotFound)

  }

  def save(ean: Long) = Action(parse.json) {
    implicit request =>
      val json = request.body
      json.validate[Product].fold(
        valid = {
          product =>
            Product.save(product)
            Ok(Json.toJson("Saved"))
        },
        invalid = {
          errors => BadRequest(Json.toJson(errors))
          //        errors => BadRequest(JsError.toFlatJson(errors))
        }
      )
  }


  def search(ean: Long) = Action {
    implicit request =>
      Ok(Json.prettyPrint(Json.toJson(Product.findByEan(ean))))
  }

  //todo complete me
  def search(searchString: String) = TimeElapsed {
    Logging {
      Action.async {
        implicit val timeout = Timeout(50000 milliseconds)

        WS.url("elasticsearch path/" + searchString + "?").withQueryString("channelId" -> searchString).get().map {
          response =>
            Ok(Json.prettyPrint(response.json))


        }
      }
    }
  }


  def edit(ean: Long) = Action {
    NotImplemented
  }

  def update(ean: Long) = Action {
    NotImplemented
  }

  /**
   *
   * Invoked on Application start
   *
   * @param address
   * @return
   */
  def fetchLatitudeAndLongitude(address: String): Option[(Double, Double)] = {
    implicit val timeout = Timeout(50000 milliseconds)

    // Encoded the address in order to remove the spaces from the address (spaces will be replaced by '+')
    //@purpose There should be no spaces in the parameter values for a GET request
    val addressEncoded = URLEncoder.encode(address, "UTF-8");
    //cache the response
    Cache.getOrElse("geocode", 10) {

      val jsonContainingLatitudeAndLongitude = WS.url("http://maps.googleapis.com/maps/api/geocode/json?address=" + addressEncoded + "&sensor=true").get().map {
        response => (response.json \\ "location")
      }

      // Wait until the future completes (Specified the timeout above)

      val result = Await.result(jsonContainingLatitudeAndLongitude, timeout.duration).asInstanceOf[List[JsObject]]

      //Fetch the values for Latitude & Longitude from the result of future
      val latitude = (result(0) \\ "lat")(0).toString.toDouble
      val longitude = (result(0) \\ "lng")(0).toString.toDouble
      Option(latitude, longitude)
    }
  }

  /**
   *
   * Calls NAP product service API
   *
   * @param resourceType
   * @param channelId
   *
   * @return json of resource from PS API
   */
  def getProduct(resourceType: String, channelId: String) = TimeElapsed {
    Logging {
      Action.async {
        implicit val timeout = Timeout(50000 milliseconds)

        //TODO create cache key function as passing dynamic data through
        //Cache.getOrElse("products", 10) {

          WS.url("http://products.api.net-a-porter.com/" + resourceType + "?").withQueryString("channelId" -> channelId).get().map {
            response =>
              Ok(Json.prettyPrint(response.json))
          }.recover {
            case e: Exception =>
              Ok(Json.prettyPrint(Json.toJson(Map("error" -> Seq(e.getMessage))))


              )
          }


     //   }

        /*    val myActor = Akka.system.actorOf(Props[ProductsActor], name = "productactor")
            (myActor ? FetchProducts(resourceType, channelId)).mapTo[Response].map(
              response =>
                Ok(Json.prettyPrint(response.json))
            )
          }*/

        /*  val responseFuture = WS.url("http://products.api.net-a-porter.co/" + resourceType + "?").withQueryString("channelId" -> channelId).get()

          val resultFuture = responseFuture map { response =>
            response.status match {
              case 200 => Some(response.json)
              case _ => None
            }
          }*/


      }
    }
  }


  implicit val JsPathWrites = Writes[JsPath](p => JsString(p.toString))

  implicit val ValidationErrorWrites =
    Writes[ValidationError](e => JsString(e.message))

  implicit val jsonValidateErrorWrites = (
    (JsPath \ "path").write[JsPath] and
      (JsPath \ "errors").write[Seq[ValidationError]]
      tupled
    )

}







