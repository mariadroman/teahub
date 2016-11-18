package services

import org.joda.time.DateTime
import scala.concurrent.Future
import play.api.libs.json._

/**
  * This is the interface of the Toggl service which is supposed to get the list of projects in a workspace.
  */
trait TogglService {

  /**
    * Get a list of the project names in a Toggl workspace
    * @param ApiToken
    * @return a Future list of project names
    */
  def getTogglProjects(ApiToken: String): Future[List[String]]
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
    **/
  case class Project(id: Long, wid: Long, cid: Long, name: String,
                     billable: Boolean, is_private: Boolean, active: Boolean,
                     template: Boolean, at: DateTime, created_at: DateTime,
                     color: String, auto_estimates: Boolean, actual_hours: Int, hex_color: String)

  implicit val dateReads = Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZZ")
  implicit val dateWrites = Writes.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss")
  implicit val projectFormat: Format[Project] = Json.format[Project]

}
