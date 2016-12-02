package services.impl

import play.api.cache.CacheApi
import play.api.http.HeaderNames
import play.api.libs.ws.WSClient
import services.GitHubService
import scala.concurrent.{ExecutionContext, Future}

/**
  * Implementation of [[GitHubService]]
  * @param ws the WS client
  * @param cacheApi gives access to application cache
  * @param ec the execution context for asynchronous execution of program logic
  */
class ApiGitHubService(ws: WSClient, cacheApi: CacheApi) (implicit val ec: ExecutionContext) extends GitHubService {

  /**
    * Get the list of GitHub projects
    * @param oauthToken the OAuth token to use in order to acess a certain repository
    * @return a Future List of github repositories
    */
  override def getGitHubProjects(oauthToken: Option[String]): Future[List[String]] = {
    oauthToken match {
      case Some(token) => {
        val request = ws.url("https://api.github.com/user/repos")
          .withHeaders(HeaderNames.AUTHORIZATION -> s"token $token")

         request.get.map{ response =>
          response.json.as[List[GitHubService.Repo]].map(x => x.name)
        }
      }
      case None => Future(List.empty)
    }
  }
}
