package models

import _root_.repository.Products
import play.api.libs.json._
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session


case class Product(id: Option[Long], ean: Long, name: String, description: String)


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

  implicit val productWrites = new Writes[Product] {
    def writes(p: Product): JsValue = {
      Json.obj(
        "ean" -> p.ean,
        "name" -> p.name,
        "description" -> p.description
      )
    }
  }

}


