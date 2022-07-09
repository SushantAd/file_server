package com.demo.file.server.util

import akka.actor.Scheduler

import java.util.concurrent.ConcurrentHashMap
import scala.collection.concurrent
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters.ConcurrentMapHasAsScala

class RateLimiterChecker(scheduler: Scheduler)(implicit val executionContext: ExecutionContext) {

  val map = RateLimiterChecker.concurrentMap

  private val task: Runnable = new Runnable{
    def run() {
      println("Scheduled...!"+map)
      if(map.nonEmpty) map.clear()
    }
  }

  scheduler.scheduleAtFixedRate(30.seconds, 30.seconds)(task)

  private def validateAndUpdateResourceRate(resource: String, rate: Int): Int={
    map.get(resource) match {
      case Some(requestRate) => map.put(resource, requestRate + rate); (requestRate + rate)
      case _=> map.put(resource, 0); 0
    }
  }

  def incrementAndGet(resource: String) = validateAndUpdateResourceRate(resource, 1) + 1
  def decrementAndGet(resource: String) = validateAndUpdateResourceRate(resource, -1) - 1

}

object RateLimiterChecker{
  val concurrentMap: concurrent.Map[String, Int] = new ConcurrentHashMap[String, Int].asScala
}
