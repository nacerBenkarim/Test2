import play.Logger
import play.api.{Application, GlobalSettings}

/**
 * Created by Benabdelkerim Mourad on 12.02.2017.
 */
object Global extends GlobalSettings {
  /**
   * This is executed at the start of the application
   * @param app
   */
  override def onStart(app:Application): Unit = {
    Logger.info("Server starts")
  }

  /**
   * This is executed when the application stops
   * @param app
   */
  override def onStop(app:Application): Unit = {
    Logger.info("System is down")
  }
}
