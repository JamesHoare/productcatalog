package controllers


import play.api.mvc.{Action, Controller}
import models.Product
import play.api.libs.json._
import play.api.Logger
import repository.Products



object Products extends Controller {

  def list = Action {

    Logger.debug((new Products()).ddl.createStatements.mkString)

    /*implicit request =>*/

      Ok(Json.prettyPrint(Json.toJson(Product.findAll)))
  }


  def details(ean: Long) = Action {
    implicit request =>

      Product.findByEan(ean).map {
        product =>
          Ok(views.html.products.details(product))
      }.getOrElse(NotFound)

  }


  def search(id: Long) = Action {
    implicit request =>
      Ok(Json.prettyPrint(Json.toJson(Product.findByEan(id))))
  }


  def edit(ean: Long) = Action {
    NotImplemented
  }

  def update(ean: Long) = Action {
    NotImplemented
  }

}
