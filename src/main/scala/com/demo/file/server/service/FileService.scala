package com.demo.file.server.service

import com.demo.file.server.util.ConfigUtil
import com.typesafe.scalalogging.LazyLogging

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters.CollectionHasAsScala

object FileService extends LazyLogging{

  def createFile(requestId: String, fileContent: String): Option[String] ={
    val path= s"${ConfigUtil.centralDirectory}\\$requestId"
    logger.info(s"File Servive request started to create file: ${requestId}")
    val resultPath = Files.write(Paths.get(s"$path.txt"), s"${fileContent}".getBytes(StandardCharsets.UTF_8))
    checkAndGetFile(resultPath)
  }

  private def checkAndGetFile(path: Path): Option[String] ={
    if(Files.exists(path))
      Some(Files.readAllLines(path).asScala.mkString)
    else
      None
  }



}
