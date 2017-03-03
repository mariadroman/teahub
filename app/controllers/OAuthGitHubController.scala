package controllers

import play.api.mvc._
import services.impl.ApiOAuthGitHubService
import scala.concurrent.{ ExecutionContext, Future }
import routes.TEAHubController

/**
 * This is the controller responsible for the actions related to OAuth authentication between TEAHub and GitHub.
 * @param oauthGitHubService helper service for authentication
 * @param executionContext the execution context for asynchronous execution of program logic
 */
class OAuthGitHubController(oauthGitHubService: ApiOAuthGitHubService)(implicit executionContext: ExecutionContext) extends Controller {

  /**
   * Make the initial request to authenticate via GitHub with OAuth protocol. The response will be sent to the
   * `redirect_url` and will be handled by the [[callback()]] function
   * @return Renders the login page and creates a session with the `oauth-state` in it.
   */
  def login = Action.async { implicit request =>
    val (connectURL, state) = oauthGitHubService.oauthGitHubConnectUrl
    Future.successful(Ok(views.html.index("Log-in to TEAHub", connectURL)).
      withSession("oauth-state" -> state))
  }

  /**
   * The URL in our application where users will be sent after authorization will trigger this action. In the URL the
   * code and state parameters are filled in by GitHub.
   * @param codeOpt is the code received when requesting access to GitHub.
   * @param stateOpt an unguessable random string. It is used to protect against cross-site request forgery attacks.
   * @return The list of GitHub repositories if the authentication was successful.
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
      } else {
        Future.successful(BadRequest("Invalid github login"))
      }
    }).getOrElse(Future.successful(Redirect(controllers.routes.OAuthGitHubController.login))) // when using back button from /main to /callback we are sent to login page
  }

  /**
   * If authentication was successful returns the projects in GitHub.
   * @return A json object containing the list of GitHub repositories.
   */
  def success() = Action.async { request =>
    request.session.get("oauth-token").fold(Future.successful(Unauthorized("401 Authentication token not found!"))) { authToken =>
      Future(Redirect(TEAHubController.githubRepositories()))
    }
  }
}
