package services.impl

import java.util.UUID
import com.typesafe.config.Config
import play.api.http.{ HeaderNames, MimeTypes }
import play.api.libs.ws.WSClient
import play.api.mvc.Results
import scala.concurrent.{ ExecutionContext, Future }

/**
 * Service providing helper functions to OAuthGitHubController
 * @param config configuration of the application
 * @param ws the WS client
 * @param executionContext the execution context for asynchronous execution of program logic
 */
class ApiOAuthGitHubService(config: Config, ws: WSClient)(implicit executionContext: ExecutionContext) {

  val baseUrl = config.getString("github.oauth.url")
  val githubClientId = config.getString("github.client.id")
  val githubClientSecret = config.getString("github.client.secret")
  val githubRedirectURL = config.getString("github.redirect.url")
  val scope = config.getString("github.client.scope")

  /**
   * Create the url with the required parameters to connect initiate OAuth authentication between TEAHub and GitHub.
   * @return a tuple containing the complete URL and the `state` parameter used in the request.
   */
  def oauthGitHubConnectUrl: (String, String) = {
    val state = UUID.randomUUID().toString
    (baseUrl.format(githubClientId, githubRedirectURL, scope, state), state)
  }

  /**
   * Get the access token in order to access GitHub.
   * @param code is the code received when requesting access to GitHub.
   * @return the access token to authenticate access to GitHub
   */
  def getToken(code: String): Future[String] = {
    val tokenResponse = ws.url("https://github.com/login/oauth/access_token").
      withQueryString(
        "client_id" -> githubClientId,
        "client_secret" -> githubClientSecret,
        "code" -> code
      ).
        withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON).
        post(Results.EmptyContent())

    tokenResponse.flatMap { response =>
      (response.json \ "access_token").asOpt[String].fold(Future.failed[String](
        new IllegalStateException("Sod off!")
      )) { accessToken =>
        Future.successful(accessToken)
      }
    }
  }
}
