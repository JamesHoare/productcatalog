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

  def cached() = Action.async {

    implicit val context = scala.concurrent.ExecutionContext.Implicits.global

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
        .withQueryString("q" -> query, "rpp" -> results.toString).get().map { response =>
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
