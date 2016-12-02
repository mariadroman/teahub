package controllers

import java.util.concurrent.TimeUnit

import play.api.cache.CacheApi
import play.api.cache._
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.impl.ApiOAuthGitHubService

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

/**
  * This is the controller responsible for the actions related to OAuth authentication between TEAHub and GitHub.
  * @param oauthGitHubService
  * @param ws the WS client
  * @param cacheApi gives access to application cache
  * @param executionContext the execution context for asynchronous execution of program logic
  */
class OAuthGitHubController(oauthGitHubService: ApiOAuthGitHubService, ws: WSClient, cacheApi: CacheApi)
                           (implicit executionContext: ExecutionContext) extends Controller {

  /**
    * Make the initial request to authenticate via GitHub with OAuth protocol. The response will be sent to the
    * `redirect_url` and will be handled by the [[callback()]] function
    * @return
    */
  def login = Action.async { implicit request =>
    val (connectURL, state) = oauthGitHubService.oauthGitHubConnectUrl
    Future.successful(Ok(views.html.index("Log-in to TEAHub", connectURL)).
      withSession("oauth-state" -> state))
  }

  /**
    * The URL in our application where users will be sent after authorization will trigger this action. In the URL the
    * code and state parameters are filled in by GitHub.
    * @param codeOpt
    * @param stateOpt
    * @return
    */
  def callback(codeOpt: Option[String] = None, stateOpt: Option[String] = None) = Action.async { implicit request =>
    (for {
      code <- codeOpt
      state <- stateOpt
      oauthState <- request.session.get("oauth-state")
    } yield {
      if (state == oauthState) {
        oauthGitHubService.getToken(code).map { accessToken =>
          Redirect(routes.OAuthGitHubController.success()).withSession("oauth-token" -> accessToken)
        }.recover {
          case ex: IllegalStateException => Unauthorized(ex.getMessage)
        }
      }
      else {
        Future.successful(BadRequest("Invalid github login"))
      }
    }).getOrElse(Future.successful(BadRequest("No parameters supplied")))
  }

  /**
    * If authentication was successful returns the projects in GitHub.
    * @return
    */
  def success() = Action.async { request =>
    request.session.get("oauth-token").fold(Future.successful(Unauthorized("No way Jose"))) { authToken =>
      cacheApi.set("authToken", authToken, Duration(60, TimeUnit.SECONDS))
      Future(Redirect(routes.TEAHubController.githubRepositories()))
    }
  }
}
