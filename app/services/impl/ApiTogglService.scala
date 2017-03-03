package services.impl

import services.TogglService
import services.TogglService.{ Project, Workspace }
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSAuthScheme.BASIC
import play.api.libs.json._
import scala.concurrent.{ ExecutionContext, Future }

/**
 * Implementation of [[TogglService]]
 * @param ws the WS client
 * @param ec the execution context for asynchronous execution of program logic
 */
class ApiTogglService(ws: WSClient)(implicit val ec: ExecutionContext) extends TogglService {

  /**
   * Request Toggl the list of all projects inside the specified workspace
   * @param apiToken to access a certain Toggl workspace
   * @return the list of all Toggle projects in the workspace
   */
  override def getTogglProjects(apiToken: String): Future[List[Project]] = {
    for {
      workspaceID <- getTogglWorkspace(apiToken)
      request <- ws.url(s"https://www.toggl.com/api/v8/workspaces/" + workspaceID.head + "/projects")
        .withHeaders("Content-Type" -> "application/Json")
        .withAuth(apiToken, "api_token", BASIC).get().map { response =>
          val bodyJSValue: JsValue = Json.parse(response.body)
          val validateBody = bodyJSValue.validate[List[Project]]
          validateBody match {
            case JsSuccess(projectList: List[Project], _) => projectList
            case JsError(_) => List.empty
          }
        }

    } yield request
  }

  /**
   * Gets the workspace ID
   *
   * @param apiToken API token provided by the user
   * @return List that always contains a single element: the workspace ID
   */
  override def getTogglWorkspace(apiToken: String): Future[List[Long]] = {
    val request = ws.url("https://www.toggl.com/api/v8/workspaces")
      .withHeaders("Content-Type" -> "application/Json")
      .withAuth(apiToken, "api_token", BASIC)

    request.get().map { response =>
      val bodyJSValue: JsValue = Json.parse(response.body)
      val validateBody: JsResult[List[Workspace]] = bodyJSValue.validate[List[Workspace]]
      validateBody match {
        case JsSuccess(workspaceList: List[Workspace], _) =>
          workspaceList.map(_.id)
        case JsError(_) => // TODO: The value of the api-token should be read from DB or we should show an error page to
          // the user for entering wrong api-toke
          List.empty
      }
    }
  }
}
