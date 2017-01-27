package services.impl

import services.TogglService
import services.TogglService.{Project, Workspace}

import play.api.libs.ws.WSClient
import play.api.libs.ws.WSAuthScheme.BASIC
import play.api.libs.json._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

/**
  * Implimatation of [[TogglService]]
  * @param ws the WS client
  * @param ec the execution context for asynchronous execution of program logic
  */
class ApiTogglService(ws: WSClient)(implicit val ec: ExecutionContext) extends TogglService {

  /**
    * Request Toggl the list of all projects inside the specified workspace
    * @param apiToken to access a certain Toggl workspace
    * @return the list of all Toggle projects in the workspace
    */
  override def getTogglProjects(apiToken: String): Future[List[String]] = {
    // Getting the workspace ID
    for {
      workspaceID <- getTogglWorkspace(apiToken)
      request <- ws.url(s"https://www.toggl.com/api/v8/workspaces/" + workspaceID.head + "/projects")
        .withHeaders("Content-Type" -> "application/Json")
        .withAuth(apiToken, "api_token", BASIC).get().map { response =>
        val bodyJSValue: JsValue = Json.parse(response.body)
        val validateBody = bodyJSValue.validate[List[Project]]
        ws.close()
        validateBody match {
          case JsSuccess(projectList: List[Project], _) => projectList.map(_.name)
          case JsError(_) => List.empty
        }
      }

    } yield request
  }

  override def getTogglWorkspace(apiToken: String): Future[List[Long]] = {
    val request = ws.url("https://www.toggl.com/api/v8/workspaces")
      .withHeaders("Content-Type" -> "application/Json")
      .withAuth(apiToken, "api_token", BASIC)
System.out.println("JOSEEEEEE "+apiToken)
    request.get().map { response =>
      val bodyJSValue: JsValue = Json.parse(response.body)
      val validateBody = bodyJSValue.validate[List[Workspace]]
      validateBody match {
        case JsSuccess(workspaceList: List[Workspace], _) =>
          workspaceList.map(_.id)
        case JsError(_) =>
          List.empty
        case _ =>
          List.empty
      }
    }
  }
}
