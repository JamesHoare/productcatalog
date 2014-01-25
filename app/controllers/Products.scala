package controllers

import models.{Tweet, Product}
import play.api.libs.json._
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.ws.WS
import play.Logger
import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import play.api.Play.current
import scala.concurrent.Await
import java.net.URLEncoder
import play.api.libs.ws.WS
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._


object Products extends Controller {

  def list = Action {

    implicit request =>




      Ok(Json.toJson(Product.findAll))
  }


  def details(ean: Long) = Action {
    implicit request =>

      Product.findByEan(ean).map {
        product =>
          Ok(views.html.products.details(product))
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


  def cached() = Action.async {


    val results = 3
    val query = "paperclip OR \"paper clip\""

    Cache.getOrElse("tweets", 10) {
      /*val responsePromise = WS.url("http://search.twitter.com/search.json")
        .withQueryString("q" -> query, "rpp" -> results.toString).get*/

      Logger.info("Requesting tweets from Twitter")

      /* responsePromise.map {
         response =>
           val tweets = Json.parse(response.body).as[Seq[Tweet]]
           Ok(Json.toJson(tweets))
       }*/

      WS.url("http://search.twitter.com/search.json")
        .withQueryString("q" -> query, "rpp" -> results.toString).get().map {
        response =>
          Ok("Feed title: " + (response.json \ "title").as[String])
      }

      /* val futureResult: Future[JsResult[Person]] = WS.url(url).get().map {
         response => (response.json \ "person").validate[Person]
       }*/

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
