package com.demo.file.server.model

import spray.json.DefaultJsonProtocol.jsonFormat3
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

final case class FileResponse(requestId: String, created: Boolean= false, fileContent: Option[String]= None)

object FileResponse{
  implicit val responseFormat: RootJsonFormat[FileResponse] = jsonFormat3(FileResponse.apply)
}