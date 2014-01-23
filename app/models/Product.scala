package models

import _root_.repository.Products
import play.api.libs.json._
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import play.Logger
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError


case class Product(ean: Long, name: String, description: String)


object Product {

  val table = new Products

  /**
   * Returns the product with the given EAN code.
   */
  def findByEan(ean: Long): Option[Product] = DB.withSession {
    implicit session: Session =>
      Query(table).filter(_.ean === ean).list.headOption
  }


  /**
   * Returns all products sorted by EAN code.
   */
  def findAll: List[Product] = DB.withSession {
    implicit session =>
      Query(table).sortBy(_.ean).list
  }

  def save(product: Product) {
    Logger.info("Product saved: " + product.name)
  }


  implicit object ProductWrites extends Writes[Product] {
    def writes(p: Product) = Json.obj(
      "ean" -> Json.toJson(p.ean),
      "name" -> Json.toJson(p.name),
      "description" -> Json.toJson(p.description)
    )
  }

  /**
   * Parses a JSON object
   */
  implicit val productReads: Reads[Product] = (
    (JsPath \ "ean").read[Long] and
      (JsPath \ "name").read[String](minLength[String](10)) and
      (JsPath \ "description").read[String](minLength[String](10))
    )(Product.apply _)




}


