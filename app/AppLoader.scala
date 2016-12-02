import controllers.TEAHubController
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.api.ApplicationLoader.Context
import play.api.cache.EhCacheComponents
import play.api.libs.ws.ahc.AhcWSClient
import services.impl.ApiTogglService
import scala.concurrent.ExecutionContext
import router.Routes

/**
  * Instantiates all parts of the application and wires everything together.
  */
class AppLoader extends ApplicationLoader {
  /** Loads the application given the context
    *
    * @param context is the context for loading an application. It includes Environment, initial configuration,
    *                web command handler, and optional source mapper
    **/
  override def load(context: Context): Application = {
    implicit val ec: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new AppComponent(context).application
  }
}

/** Provides all the built in components dependencies from the application loader context
  *
  * @param context is the context for loading an application. It includes Environment, initial configuration,
  *                web command handler, and optional source mapper */
class AppComponent(context: Context)(implicit val ec: ExecutionContext) extends BuiltInComponentsFromContext(context)
  with EhCacheComponents{
  lazy val togglService = new ApiTogglService(AhcWSClient())
  lazy val teahubController = new TEAHubController(togglService,defaultCacheApi)
  lazy val assetsController = new controllers.Assets(httpErrorHandler)

  lazy val router = new Routes(httpErrorHandler,
    teahubController,
    assetsController
  )
}
