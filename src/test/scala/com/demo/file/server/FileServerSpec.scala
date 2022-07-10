package com.demo.file.server

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethod, StatusCodes}
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.http.scaladsl.server._
import Directives._
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import com.demo.file.server.model.{FileRequest, FileResponse}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.{JsValue, enrichAny}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpMethods.{GET, POST}
import com.demo.file.server.FileServer.PathBusyRejection
import spray.json.DefaultJsonProtocol._

import scala.concurrent.duration.DurationInt

class FileServerSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  lazy val testKit = ActorTestKit()
  implicit def typedSystem = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem = testKit.system.classicSystem

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(10.seconds)

  val url = "/api/server/create"

  val requestBody: JsValue = FileRequest(requestId = "test1").toJson

  val responseBody = FileResponse("test1", true, Some("test1_random")).toJson

  val wrongRequest = Map("wrong"-> "request").toJson

  def requestEntity(request: JsValue) = HttpEntity(
    ContentTypes.`application/json`,
    request.toString()
  )

  val testRoute =
    pathPrefix("api") {
      pathPrefix("server") {
        post {
          path("create") {
            complete(responseBody)
          }
        }
      }
    }

  val rejectRoute =
    pathPrefix("api") {
      pathPrefix("server") {
        post {
          path("create") {
            complete(StatusCodes.TooManyRequests)
          }
        }
      }
    }

  val errorRoute =
    pathPrefix("api") {
      pathPrefix("server") {
        post {
          path("create") {
            complete(StatusCodes.InternalServerError)
          }
        }
      }
    }

  "File Server" should{

    "return 200 with fileContent for successful file creation" in{
      Post(url).withEntity(requestEntity(requestBody)) ~> testRoute ~> check{
        entityAs[FileResponse].toJson shouldEqual responseBody
      }
    }

    "return 429 statusCode with empty response for too many request" in{
      Post(url).withEntity(requestEntity(requestBody)) ~> rejectRoute ~> check{
        status shouldEqual StatusCodes.TooManyRequests
      }
    }

    "return 500 internal server error for wrong request" in{
      Post(url) ~> errorRoute ~> check{
        status shouldEqual StatusCodes.InternalServerError
      }
    }
  }

}
