import datamappers.SparkDriver
import models.ResultRow
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

/**
 * Created by clamou on 12.02.2017.
 */

class TopMoviesSpec extends FeatureSpec with GivenWhenThen with Matchers {
  info("As a user")
  info("I want to get the top 100 movies")
  info("After that filter by ages range and genre")

  feature("Top 100 movies"){
      scenario("Get movies without filters"){
          Given("A user visit the app for the first time")
          Then("Ask spark for the top 100 movies")
          val topMoviesWithoutFilter:List[ResultRow] = SparkDriver.filterByDefault
          topMoviesWithoutFilter should have size 100
      }
  }
}
