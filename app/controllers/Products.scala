package controllers


import play.api.mvc.{Action, Controller}
import models.Product
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.libs.functional._


object Products extends Controller {

  def list = Action { implicit request =>

    val products = Product.findAll

    Ok(views.html.products.list(products))
  }



  def details(ean: Long) = Action { implicit request =>

    Product.findByEan(ean).map { product =>
      Ok(views.html.products.details(product))
    }.getOrElse(NotFound)

  }


  def search(id: Long) = Action {  implicit request =>
    Ok(Json.prettyPrint(Json.toJson(Product.findByEan(id))))
  }


  private val productForm: Form[Product] = Form(
    mapping(
      "ean" -> longNumber.verifying(
        "validation.ean.duplicate", Product.findByEan(_).isEmpty),
      "name" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Product.apply)(Product.unapply)
  )

}
