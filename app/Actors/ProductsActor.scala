package Actors

import akka.actor.{ActorLogging, Actor}
import play.api.libs.ws.WS
import controllers.Products.FetchProducts

/**
 * Simple Actor that calls product api
 */
class ProductsActor extends Actor with ActorLogging {


  def receive = {
    case FetchProducts =>
      val futureResponse = WS.url("http://products.api.net-a-porter.com/" + FetchProducts.resourceType + "?").withQueryString("channelId" -> FetchProducts.channelId).get()
        sender ! futureResponse

      //log.debug("body****" + response)


  }

}