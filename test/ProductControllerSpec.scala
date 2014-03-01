package test

import controllers.Products
import play.api.test.{FakeRequest, PlaySpecification}
import play.mvc.{SimpleResult, Controller, Results}
import scala.concurrent.Future


object ProductControllerSpec extends PlaySpecification with Results {

  object Products extends Controller

  "Example Page#index" should {
    "should be valid" in {
      val controller = new Products()
      val result: Future[SimpleResult] = controller.getProduct("","").apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must be equalTo "ok"
    }
  }
}