package controllers

import play.api.cache.CacheApi
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc._
import services.TogglService
import services.impl.ApiGitHubService
import scala.concurrent.{ExecutionContext, Future}

/**
  * This is the controller responsible for the actions related to communication between TEAHub and GitHub/Toggl.
  * @param togglService the service that makes the requests to Toggl
  * @param apiGitHubService the service that makes the requests to GitHub
  * @param cacheApi the application cache
  * @param executionContext the execution context for asynchronous execution of program logic
  */
class TEAHubController(togglService: TogglService, apiGitHubService: ApiGitHubService, cacheApi: CacheApi)
                      (implicit executionContext: ExecutionContext) extends Controller {

  /**
    * Get list of Toggl projects
    * @return A json object containing the list of Toggl projects
    */
  //TODO: This method just takes the project names; in future it should show the result in the related page in TEAHUB.
  def togglProjects = Action.async { implicit request =>
    val toggleToken: Option[String] = request.getQueryString("apiToken")
    val result = toggleToken match {
      case Some(token) => togglService.getTogglProjects(token)
      case None => Future.successful(List.empty)
    }
    result.map {
      theResult =>
        Ok(Json.obj("Projects" -> theResult))
    }
  }

  /**
    * Get list of GitHub repositories
    * @return A json object containing the list of GitHub projects.
    */
  def githubRepositories = Action.async { implicit request =>
    val oauthToken = cacheApi.get("authToken")
    val list: Future[List[String]] = apiGitHubService.getGitHubProjects(oauthToken)
    list.map {res => Ok(Json.obj("repositories" -> res))}
  }

}
