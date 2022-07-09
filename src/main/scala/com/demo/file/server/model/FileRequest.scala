package com.demo.file.server.model

import spray.json.DefaultJsonProtocol.jsonFormat3
import spray.json.DefaultJsonProtocol._

final case class FileRequest(requestId: String)

object FileRequest{
  implicit val requestMarshaller : spray.json.RootJsonFormat[FileRequest] = jsonFormat1(FileRequest.apply)
}