package controllers

import play.api.libs.json.Json
import play.api.cache.CacheApi
import play.api.mvc.Action
import play.api.mvc._
import services.TogglService
import scala.concurrent.{ExecutionContext, Future}

/** Is a controller for the project
  *
  * @param togglService Service that requests Toggl the list of the projects */
class TEAHubController(togglService: TogglService, cache: CacheApi)(implicit executionContext: ExecutionContext) extends Controller {
  /** Request List of Toggl projects' name from Toggl API and show it in TEAHub UI
    */
  //TODO: So far this method just takes the projects name in future it should show the result in the related page in TEAHUB
  def togglProjects = Action.async { implicit request =>

        def cacheKey(apiKey: String) = s"ProjectName.$apiKey"

        val toggleToken: Option[String] = request.getQueryString("apiToken")

        val result: Future[List[String]] = toggleToken match {
          case Some(token) => {
            cache.get[Future[List[String]]](cacheKey(token)) match {
              case None =>
                val projectsName = togglService.getTogglProjects(token)
                cache.set(cacheKey(token), projectsName)
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
  }
