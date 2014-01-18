package controllers


import play.api.mvc.{Action, Controller}
import models.Product
import play.api.libs.json._
import play.api.data.validation.ValidationError
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._



object Products extends Controller {

  def list = Action {

    //Logger.debug((new Products()).ddl.createStatements.mkString)

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

  def save = Action(parse.json) { implicit request =>
    val json = request.body
    json.validate[Product].fold(
      valid = { product =>
        Product.save(product)
        Ok("Saved")
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

  implicit val JsPathWrites = Writes[JsPath](p => JsString(p.toString))

  implicit val ValidationErrorWrites =
    Writes[ValidationError](e => JsString(e.message))

  implicit val jsonValidateErrorWrites = (
    (JsPath \ "path").write[JsPath] and
      (JsPath \ "errors").write[Seq[ValidationError]]
      tupled
    )

}
