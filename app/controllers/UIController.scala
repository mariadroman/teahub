package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

/**
  * This controller is mainly responsible for linking the mockups.
  * @param messagesApi the messagesAPI for internationalisation.
  * @param executionContext the execution context for asynchronous execution of program logic
  */
class UIController(val messagesApi: MessagesApi)(implicit executionContext: ExecutionContext) extends Controller with I18nSupport {
  val togglTokenForm = Form(single("togglToken" -> text()))
  val projectName = Form(single("projectName" -> text()))

  def management = Action { implicit request => Ok(views.html.user_management()) }
  def list = Action { implicit request => {
      togglTokenForm.bindFromRequest().fold(
        error => BadRequest, // Warning here because 'error' is never used
        data => {
          System.out.println("JOSE: the togglToken is " + data) // TODO: togglToken should be put inside the cache
          Ok(views.html.projects())
        }
      )
    }
  }
  def setup = Action { implicit request => Ok(views.html.setup_projects(togglTokenForm)) }
  def details = Action { implicit request => Ok(views.html.project_details()) }
  def newProject = Action { implicit request => Ok(views.html.new_project(projectName)) }
  def issues = Action { implicit request => Ok(views.html.issues()) }
  def profile = Action { implicit request => Ok(views.html.profile()) }

}
