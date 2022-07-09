package com.demo.file.server

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive0, Rejection, RejectionHandler}
import akka.http.scaladsl.server.Directives.{as, complete, entity, extractRequest, handleRejections, mapResponse, onSuccess, path, pathPrefix, post, reject}
import akka.pattern.ask
import akka.util.Timeout
import com.demo.file.server.actor.FileActor
import com.demo.file.server.model.FileRequest
import spray.json.JsValue
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.demo.file.server.util.{Config, RateLimiterChecker}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


object FileServer {

  implicit val system: ActorSystem = ActorSystem()
  val fileActor: ActorRef = system.actorOf(Props[FileActor])
  val rateLimiterChecker = new RateLimiterChecker(system.scheduler)

  case object PathBusyRejection extends Rejection

  class Limiter(max: Int) {

    // needs to be a thread safe counter since there can be concurrent requests

    def limitConcurrentRequests(req: FileRequest): Directive0 =
      extractRequest.flatMap { request => //to use request elements to create unique resoource
        if (rateLimiterChecker.incrementAndGet(req.requestId) > max) {
          rateLimiterChecker.decrementAndGet(req.requestId)
          reject(PathBusyRejection)
        } else {
          mapResponse { response =>
            rateLimiterChecker.decrementAndGet(req.requestId)
            response
          }
        }
      }
  }

  def main(args: Array[String]): Unit = {
    val rejectionHandler = RejectionHandler.newBuilder()
      .handle {
        case PathBusyRejection =>
          complete((StatusCodes.TooManyRequests, ""))
      }.result()


    val limiter = new Limiter(max = Config.maxRequest)

    val route =
      handleRejections(rejectionHandler) {
        pathPrefix("api") {
          pathPrefix("server") {
            post {
              path("create") {
                entity(as[FileRequest]) { request =>
                  limiter.limitConcurrentRequests(request) {
                    implicit val timeout: Timeout = Config.maxTimeout.seconds
                    onSuccess((fileActor ? FileRequest(request.requestId)).mapTo[JsValue]) { res =>
                      complete(res)
                    }
                  }
                }
              }
            }
          }
        }
      }

    Http().newServerAt("127.0.0.1", 8080).bind(route).onComplete {
      case Success(_) => println("Listening for requests on http://127.0.0.1:8080")
      case Failure(ex) =>
        println("Failed to bind to 127.0.0.8080")
        ex.printStackTrace()
    }
  }


}
