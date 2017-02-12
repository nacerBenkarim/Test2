package configuration

import java.io.File

import play.api.Play

/**
 * Created by Benabdelkerim Mourad on 12.02.2017.
 */
object Config {

  val CONFIG = play.api.Configuration.load(new File("."))

  val DB_URL = CONFIG.getString("app.database.url").getOrElse("jdbc:mysql://127.0.0.1/xing")
  val DB_USERNAME = CONFIG.getString("app.database.username").getOrElse("root")
  val DB_PASSWORD = CONFIG.getString("app.database.username").getOrElse("")
  val DB_DRIVER = CONFIG.getString("app.database.driver").getOrElse("")

  val MAX_RESULT = CONFIG.getInt("app.spark.maxResult").getOrElse(100)
  val SPARK_LOW_BOUND = CONFIG.getInt("app.spark.lowBound").getOrElse(0)
  val SPaRK_UPPER_BOUND = CONFIG.getInt("app.spark.upperBound").getOrElse(100)
  val SPARK_NUMBER_PARTITIONS = CONFIG.getInt("app.spark.numberPartitions").getOrElse(2)

  val SPARK_EXECUTOR_MEMORY = CONFIG.getString("app.spark.executorMemory").getOrElse("1g")
  val SPARK_AKKA_TIMEOUT = CONFIG.getInt("app.spark.timeout").getOrElse(5)
  val SPARK_ALLOW_MULTIPLE_CONTEXTS = CONFIG.getBoolean("app.spark.allowMultipleContexts").getOrElse(true)
  val SPARK_ADDRESS = CONFIG.getString("app.spark.adress").getOrElse("local")
  val SPARK_NUMBER_CORES = "[" + CONFIG.getInt("app.spark.numberCores").getOrElse(2) +"]";
  val APPLICATION_NAME = CONFIG.getString("application.name").getOrElse("app")
}
