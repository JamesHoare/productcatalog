package repository

import play.api.db.slick.Config.driver.simple._
import models.{Product}

/**
 *
 * Slick definition for Product Table
 *
 */
class Products extends Table[Product]("PRODUCTS") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def ean = column[Long]("ean")
  def name = column[String]("name")
  def description = column[String]("description")

  def * = id.? ~ ean ~ name ~ description <> (mapRow _, unMapRow _)

  private def mapRow(id: Option[Long], ean: Long, name: String, description: String): Product = {
    Product(id, ean, name,description)
  }

  private def unMapRow(product: Product) = {
    val tuple = (product.id, product.ean, product.name, product.description)
    Some(tuple)
  }




}

