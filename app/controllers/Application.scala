package controllers

import _root_.datamappers.SparkDriver
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.text

object Application extends Controller {
  /**
   * Index page
   * @return Action
   */
  def index = Action {
    var genre =List[String]()
    var ages =List[String]()
    val (result, elapsedTime) = profile {
      SparkDriver.filterByDefault
    }
    Ok(views.html.index(result, elapsedTime))
  }

  /**
   *  Calculate elapsed time.
   * @param block :  block to profile
   * @param t: when the request is triggered.
   * @tparam R :result type which is a tuple of query-result and elapsed time
   * @return tuple(query-result, Elapsed time)
   */
  private def profile[R](block: => R, t: Long = System.currentTimeMillis) = (block, System.currentTimeMillis - t)
}