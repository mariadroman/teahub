package controllers

import java.util.concurrent.TimeUnit
import play.api.libs.json.Json
import play.api.cache.CacheApi
import play.api.mvc.Action
import play.api.mvc._
import services.TogglService
import services.impl.ApiGitHubService
import scala.concurrent.duration.Duration
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

    def cacheKey(apiKey: String) = s"ProjectName.$apiKey"

    // TODO: this should be provided by the user
    val toggleToken: Option[String] = request.getQueryString("apiToken")

    val result: Future[List[String]] = toggleToken match {
      case Some(token) => {
        cacheApi.get[Future[List[String]]](cacheKey(token)) match {
          case None =>
            val projectsName = togglService.getTogglProjects(token)
            cacheApi.set(cacheKey(token), projectsName, Duration(60, TimeUnit.SECONDS))
            projectsName
          case Some(list) => list
        }

      }
      case None => Future.successful(List.empty)
    }
    result.map {
      theResult =>
        Ok(Json.obj("Projects" -> theResult))
    }
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
