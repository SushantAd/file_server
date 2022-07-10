package com.demo.file.server.actor

import akka.actor.Actor
import com.demo.file.server.model.{FileRequest, FileResponse}
import com.demo.file.server.service.FileService
import spray.json.enrichAny

import scala.concurrent.duration._
import scala.util.Random


class FileActor extends Actor{
  import context.dispatcher

  def receive = {
    case FileRequest(requestId: String) =>
      val randomStr =  s"${requestId}_${(Random.alphanumeric take 10).mkString}"
      val fileCreated =
        FileService.createFile(requestId, randomStr) match {
          case Some(fileInfo) => FileResponse(requestId, true, Some(fileInfo))
          case _ => FileResponse(requestId)
        }
      context.system.scheduler.scheduleOnce(5.seconds, sender(), fileCreated.toJson) //min delay 5s as per requirement
  }
}
