package controllers

import javax.inject.{Inject, Singleton}

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import play.api.i18n.{I18nSupport, MessagesApi}

import scala.concurrent.ExecutionContext

/**
  * Controller for front end
  */
class UIController (val messagesApi: MessagesApi)(implicit executionContext: ExecutionContext) extends Controller with I18nSupport {
  val togglTokenForm = Form(single("togglToken" -> text(maxLength = 20)))
  val projectName = Form(single("projectName" -> text(maxLength = 20)))

  def index = Action { implicit request => Ok(views.html.init_index()) }
  def management = Action { implicit request => Ok(views.html.init_user_management()) }
  def list = Action { implicit request => Ok(views.html.init_projects()) }
  def setup = Action { implicit request => Ok(views.html.init_setup_projects(togglTokenForm)) }
  def details = Action { implicit request => Ok(views.html.init_project_details()) }
  def newProject = Action { implicit request => Ok(views.html.init_new_project(projectName)) }
  def usersProject = Action { implicit request => Ok(views.html.init_user_project()) }
  def issues = Action { implicit request => Ok(views.html.init_issues()) }
  def profile = Action { implicit request => Ok(views.html.init_profile()) }

}
