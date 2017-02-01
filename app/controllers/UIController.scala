package controllers

import java.util.concurrent.TimeUnit

import play.api.cache.CacheApi
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration

/**
  * This controller is mainly responsible for linking the mockups.
  *
  * @param messagesApi      the messagesAPI for internationalisation.
  * @param executionContext the execution context for asynchronous execution of program logic
  */
class UIController(val messagesApi: MessagesApi, cacheApi: CacheApi, teaHubController: TEAHubController)
                  (implicit executionContext: ExecutionContext) extends Controller with I18nSupport {
  val togglTokenForm = Form(single("togglToken" -> text()))
  val projectName = Form(single("projectName" -> text()))

  def management = Action { implicit request => Ok(views.html.user_management()) }

  def list = Action { implicit request => {
      togglTokenForm.bindFromRequest().fold(
        error => BadRequest, // Warning here because 'error' is never used
                             // TODO: some error message should be displayed on projects_setup
        data => {
          cacheApi.set("togglToken", data, Duration(1, TimeUnit.DAYS)) // TODO: in the future this will be stored on
                                                                       // the DB
          Ok(views.html.projects())
        }
      )
    }
  }

  def setup = Action { implicit request => Ok(views.html.setup_projects(togglTokenForm)) }
  def details = Action { implicit request => Ok(views.html.project_details()) }

  def newProject = Action.async { implicit request =>
    teaHubController.togglProjects.map(projects => Ok(views.html.new_project(projectName, projects)))
  }

  def issues = Action { implicit request => Ok(views.html.issues()) }
  def profile = Action { implicit request => Ok(views.html.profile()) }

}
