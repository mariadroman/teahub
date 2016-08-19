import com.typesafe.config.Config
import controllers.{OAuthGitHubController, TEAHubController, UIController}
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.api.ApplicationLoader.Context
import play.api.cache.EhCacheComponents
import play.api.libs.ws.ahc.AhcWSClient
import play.api.i18n._
import services.impl.{ApiGitHubService, ApiOAuthGitHubService, ApiTogglService}
import scala.concurrent.ExecutionContext
import router.Routes

/**
  * Instantiates all parts of the application and wires everything together.
  */
class AppLoader extends ApplicationLoader {

  /**
    * Loads the application given the context
    * @param context is the context for loading an application (includes Environment, initial configuration, web
    *                command handler and optional source mapper
    * @return the created application
    */
  override def load(context: Context): Application = {
    implicit val ec: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new AppComponent(context).application
  }
}

/**
  * Provides all the built in components dependencies from the application loader context
  * @param context is the context for loading an application.
  * @param ec implicit execution context for asynchronous execution of program logic
  */
class AppComponent(context: Context)(implicit val ec: ExecutionContext) extends BuiltInComponentsFromContext(context)
  with EhCacheComponents with I18nComponents {

  val config: Config = context.initialConfiguration.underlying

  lazy val oauthGitHubService = new ApiOAuthGitHubService(config, AhcWSClient())
  lazy val oauthGitHubController = new OAuthGitHubController(oauthGitHubService, AhcWSClient(), defaultCacheApi)
  lazy val gitHubService = new ApiGitHubService(AhcWSClient(), defaultCacheApi)
  lazy val togglService = new ApiTogglService(AhcWSClient())
  lazy val teahubController = new TEAHubController(togglService, gitHubService, defaultCacheApi)
  lazy val assetsController = new controllers.Assets(httpErrorHandler)
  lazy val uiController = new UIController(messagesApi)(ec)

  lazy val router = new Routes(
    httpErrorHandler,
    oauthGitHubController,
    uiController,
    teahubController,
    assetsController
  )
}
