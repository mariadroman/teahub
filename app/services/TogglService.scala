package services

import org.joda.time.DateTime

import scala.concurrent.Future
import play.api.libs.json._
import services.TogglService.Project

/**
 * This is the interface of the Toggl service which is supposed to get the list of projects in a workspace.
 */
trait TogglService {

  /**
   * Get a list of the project names in a Toggl workspace
   *
   * @param ApiToken The unique API token of the Toggle user
   * @return A Future list of projects
   */
  def getTogglProjects(ApiToken: String): Future[List[Project]]
  def getTogglWorkspace(ApiToken: String): Future[List[Long]]
}

/**
 * Companion object including case class model for Toggl projects, and Reads/Writes to convert from/to json format.
 */
object TogglService {

  /**
   * Model case class for Toggl Project
   *
   * @param id             Project ID
   * @param wid            Workspace ID, where the project belongs to
   * @param cid            Client ID
   * @param name           Name of the Toggl project in a specific workspace
   * @param billable       Whether the project is billable or not
   * @param is_private     Whether the project is accessible for only project users or for all workspace users
   * @param active         Whether the project is archived or not
   * @param template       Whether the project can be used as a template
   * @param at             Indicates the time task was last updated
   * @param created_at     Timestamp indicating when the project was created
   * @param color          ID of the color selected for the project
   * @param auto_estimates Whether the estimated hours are automatically calculated based on task estimations or
   *                       manually fixed based on the value of 'estimated_hours'
   * @param actual_hours   Hours that has been spent on the project
   * @param hex_color      Project tag color
   */
  case class Project(id: Long, wid: Long, cid: Long, name: String,
    billable: Boolean, is_private: Boolean, active: Boolean,
    template: Boolean, at: DateTime, created_at: DateTime,
    color: String, auto_estimates: Boolean, actual_hours: Int, hex_color: String)

  case class Workspace(
    id: Long,
    name: String,
    profile: Int,
    premium: Boolean,
    admin: Boolean,
    default_currency: String,
    only_admins_may_create_projects: Boolean,
    only_admins_see_billable_rates: Boolean,
    only_admins_see_team_dashboard: Boolean,
    projects_billable_by_default: Boolean,
    rounding: Int,
    rounding_minutes: Int,
    at: DateTime,
    logo_url: String,
    ical_url: String,
    ical_enabled: Boolean
  )

  implicit val dateReads = Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZZ")
  implicit val dateWrites = Writes.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss")
  implicit val projectFormat: Format[Project] = Json.format[Project]
  implicit val workspaceFormat: Format[Workspace] = Json.format[Workspace]

}
