package repository

import play.api.db.slick.Config.driver.simple._
import models.{Product}

/**
 *
 * Slick definition for Product Table
 *
 */
class Products extends Table[Product]("products") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def ean = column[Long]("ean")
  def name = column[String]("name")
  def description = column[String]("description")

  def * = ean ~ name ~ description <> (Product.apply _, Product.unapply _)



}

