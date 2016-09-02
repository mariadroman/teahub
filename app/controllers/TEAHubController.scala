package controllers

import play.api.mvc.Action
import play.api.mvc._
import services.TogglService
import scala.concurrent.{ExecutionContext, Future}

/** Is a controller for the project
  *
  * @param togglService Service that requests Toggl the list of the projects */
class TEAHubController(togglService: TogglService)(implicit executionContext: ExecutionContext) extends Controller {
  /** Request List of Toggl projects' name from Toggl API and show it in TEAHub UI
    *
    * @param apiToken is a unique String that identifies the Toggl users. The value can be find under My Profile in Toggl account */
  //TODO: So far this method just takes the projects name in future it should show the result in the related page in TEAHUB
  def togglProjects(apiToken: String) = Action.async { implicit request =>
    val result: Future[List[String]] = togglService.getTogglProjects(apiToken)
    Future.successful(Ok("Hello Toggl!"))
  }
}
