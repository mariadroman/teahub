package controllers

import play.api.libs.json.Json
import play.api.cache.CacheApi
import play.api.mvc.Action
import play.api.mvc._
import services.TogglService
import services.impl.ApiGitHubService
import services.TogglService.Project
import scala.concurrent.{ ExecutionContext, Future }

/**
 * This is the controller responsible for the actions related to communication between TEAHub and GitHub/Toggl.
 * @param togglService     the service that makes the requests to Toggl
 * @param apiGitHubService the service that makes the requests to GitHub
 * @param cacheApi         the application cache
 * @param executionContext the execution context for asynchronous execution of program logic
 */
class TEAHubController(togglService: TogglService, apiGitHubService: ApiGitHubService, cacheApi: CacheApi)(implicit executionContext: ExecutionContext) extends Controller {

  /**
   * Get list of Toggl projects
   * @return A json object containing the list of Toggl projects
   */
  def togglProjects: Future[List[Project]] = {
    def cacheKey(apiKey: String) = s"ProjectName.$apiKey"

    val togglToken = cacheApi.get[String]("togglToken") match {
      case None => "" // TODO: the token will be read from the DB in the future, right now is extracted from the cache.
      // Take a closer look into this later
      case Some(token) => token
    }

    togglService.getTogglProjects(togglToken)
  }

  /**
   * Get list of GitHub repositories
   * @return A json object containing the list of GitHub repositories.
   */
  def githubRepositories = Action.async { implicit request =>
    val result: Future[List[String]] = request.session.get("oauth-token").map { token =>
      apiGitHubService.getGitHubProjects(token)
    }.getOrElse(Future.successful(List.empty))

    result.map(res => Ok(Json.obj("repositories" -> res)))
  }

}
