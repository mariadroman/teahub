package services.impl

import play.api.http.HeaderNames
import play.api.libs.ws.WSClient
import services.GitHubService._
import services.GitHubService
import scala.concurrent.ExecutionContext

/**
 * Implementation of [[GitHubService]]
 * @param ws the WS client
 * @param ec the execution context for asynchronous execution of program logic
 */
class ApiGitHubService(ws: WSClient)(implicit val ec: ExecutionContext) extends GitHubService {

  /**
   * Get the list of GitHub projects
   * @param oauthToken the OAuth token to use in order to access a certain repository
   * @return a Future List of Github repositories
   */
  override def getGitHubProjects(oauthToken: String) = {
    val reposURL = "https://api.github.com/user/repos"

    ws.url(reposURL)
      .withHeaders(HeaderNames.AUTHORIZATION -> s"token ${oauthToken}").get.map { response =>
        response.json.as[List[Repo]].map(x => x.name)
      }
  }
}
