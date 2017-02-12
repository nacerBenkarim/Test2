package datamappers

import java.sql.{ResultSet, DriverManager}
import configuration.Config
import models.ResultRow
import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.{SparkContext, SparkConf}
import play.api.Logger

/**
 * Created by Benabdelkerim Mourad on 12.02.2017.
 */
private object ExtractTYPE extends Enumeration {
  val UNKNOWN=0
  val ALL =1
}

object SparkDriver {
    private val MINIMUM_RATING_AMOUNT = 20
    private val TITLE_COLUMN          = "title"
    private val AVERAGE_RATING_COLUMN = "avgRating"
    private val COUNT_COLUMN          = "ratingCount"

    private val conf = new SparkConf()
                      .setAppName(Config.APPLICATION_NAME)
                      .setMaster(Config.SPARK_ADDRESS + Config.SPARK_NUMBER_CORES)
                      .set("spark.executor.memory", Config.SPARK_EXECUTOR_MEMORY)
                      .set("spark.akka.timeout", Config.SPARK_AKKA_TIMEOUT.toString)
                      .set("spark.driver.allowMultipleContexts",Config.SPARK_ALLOW_MULTIPLE_CONTEXTS.toString)

    private val sparkContext = new SparkContext(conf)

    /**
     * Get top 100 movies that have more than 20 ratings.
     * @return List of movies
     */
    def filterByDefault:List[ResultRow] ={
        val query =
          f"""
            |SELECT *
            |FROM (
            |  SELECT
            |    m.title AS title,
            |    AVG(r.rating) AS avgRating,
            |    COUNT(r.rating) AS ratingCount
            |  FROM movies AS m
            |  INNER JOIN ratings AS r
            |	ON r.movie_id = m.id
            |  GROUP BY m.title
            |  HAVING ratingCount >= $MINIMUM_RATING_AMOUNT
            |) AS finalRatings
            |ORDER BY avgRating DESC limit  ?, ?
          """.stripMargin
        executeQuery(ExtractTYPE.ALL, query)
    }

    /**
     *  Sends sql-query to spark to execute
     * @param extractBy: Type of extraction
     * @param query:  The query must contain two ? placeholders for parameters used to partition the results,
     *                when you wan to use more than one partitions.
     *                E.g. "select title, author from books where ? <= id and id <= ?"
     *                If numPartitions is set to exactly 1, the query do not need to contain any ? placeholder.
     * @return Returns a tuple which can contain a List of ResultsRow, List of ages/genres
     */
    private def executeQuery(extractBy: Int, query:String):List[ResultRow] = {
        try {
          if(extractBy != ExtractTYPE.UNKNOWN){
              val values = new JdbcRDD(
                sparkContext, () => createConnection(),
                query,
                Config.SPARK_LOW_BOUND,
                Config.SPaRK_UPPER_BOUND,
                Config.SPARK_NUMBER_PARTITIONS,
                mapRow = extractValues
              )
              values.take(Config.MAX_RESULT).toList
          }else{
            Logger.error(f"""Invalid extraction type supplied!""")
            null
          }
        }catch{
          case e: Throwable => {
            Logger.error(
              f""" Failed to execute query:
                 |$e.printStackTrace """.stripMargin)
            null
          }
        }
    }

    /**
     * CreateConnection a function that returns an open Connection.
     * The RDD takes care of closing the connection.
     * @return an open Connection to database
     */
    private def createConnection() = {
        Class.forName(Config.DB_DRIVER).newInstance
        DriverManager.getConnection(Config.DB_URL, Config.DB_USERNAME, Config.DB_PASSWORD);
    }

    /**
     * Extract title, rating and count from result.
     *
     * @param row  a function from a ResultSet to a single row of the desired result type(s).
     *            This should only call getInt, getString, etc; the RDD takes care of calling next.
     *            The default maps a ResultSet to an array of Object.
     * @return ResultRow
     */
    private def extractValues(row: ResultSet) = {
        val title = row.getString(TITLE_COLUMN)
        val rating = (row.getDouble(AVERAGE_RATING_COLUMN) * 100).round / 100.toDouble
        val count = row.getInt(COUNT_COLUMN)
        ResultRow(title, rating, count)
    }
}
