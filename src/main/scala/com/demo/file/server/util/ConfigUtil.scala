package com.demo.file.server.util

import com.typesafe.config.{Config, ConfigFactory}

object Config {

  private val conf: Config = ConfigFactory.load()

  val maxRequest: Int = conf.getInt("request.max-request")
  val maxTimeout: Int = conf.getInt("request.max-timeout")

  val centralDirectory: String = conf.getString("file_store.central-directory")
  val defaultFileExtension: String = conf.getString("file_store.default-extension")

}
