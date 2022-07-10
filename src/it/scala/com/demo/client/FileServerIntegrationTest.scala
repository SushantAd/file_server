package com.demo.client

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.demo.file.server.model.{FileRequest, FileResponse}
import com.demo.file.server.util.ConfigUtil
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.DefaultJsonProtocol._
import spray.json.{JsValue, enrichAny}

import java.nio.file.{Files, Paths}
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt


class FileServerIntegrationTest extends AnyWordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {

  val requestId = "t_e_s_t_file_dont_use"
  val fileDirUrl = s"${ConfigUtil.centralDirectory}\\${requestId}.${ConfigUtil.defaultFileExtension}"
  val url = s"http://${ConfigUtil.applicationHost}:${ConfigUtil.applicationPort}/api/server/create"

  val requestBody: JsValue = FileRequest(requestId).toJson
  val request: HttpRequest = HttpRequest(
    method = HttpMethods.POST,
    uri = url,
    entity = HttpEntity(
      ContentTypes.`application/json`,
      requestBody.toString()
    )
  )

  override protected def beforeAll(): Unit = {
    println("Deleting test file before test : " + Files.deleteIfExists(Paths.get(fileDirUrl)))
  }

  override protected def afterAll(): Unit = {
    println("#Failesafe Deleting test file after test : " + Files.deleteIfExists(Paths.get(s"${ConfigUtil.centralDirectory}\\${requestId}")))
  }

  "File Server" should {

    "return 200-Ok with fileContent for successful file creation" in {
      val futureRequest = Http().singleRequest(request)

      val response = Await.result(futureRequest, 10.seconds)
      response.status shouldEqual StatusCodes.OK
    }

    "return 429-too many requests with empty body if more than 2 request are sent for a single resource" in {
      val futureRequest1 = Http().singleRequest(request)
      val futureRequest2 = Http().singleRequest(request)
      val futureRequest3 = Http().singleRequest(request)

      val response = Await.result(futureRequest3, 10.seconds)
      response.status shouldEqual StatusCodes.TooManyRequests
    }

    "return 400-Bad request with error for wrong post body" in {
      val request: HttpRequest = HttpRequest(
        method = HttpMethods.POST,
        uri = url,
        entity = HttpEntity(
          ContentTypes.`application/json`,
          ""
        )
      )
      val futureRequest = Http().singleRequest(request)

      val response = Await.result(futureRequest, 10.seconds)
      response.status shouldEqual StatusCodes.BadRequest
    }

  }
}
