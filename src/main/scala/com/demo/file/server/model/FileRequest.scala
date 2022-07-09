package com.demo.file.server.model

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

final case class FileRequest(requestId: String)

object FileRequest{
  implicit val requestFormat : RootJsonFormat[FileRequest] = jsonFormat1(FileRequest.apply)
}