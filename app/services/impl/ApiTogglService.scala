package services.impl

import services.TogglService
import services.TogglService.Project
import play.api.libs.ws.WSClient
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.ws.WSAuthScheme.BASIC
import play.api.libs.json._

/**
  * Implimatation of [[TogglService]]
  */
class ApiTogglService(ws: WSClient)(implicit val ec: ExecutionContext) extends TogglService {
  /** Request Toggl the list of all projects inside the specified workspace
    *
    * @return returns the list of all Toggle projects in the workspace */
  override def getTogglProjects(apiToken: String): Future[List[String]] = {
    //TODO: The workspace ID should be passed to this method. Meanwhile, in order to test change the xxxx to a proper workspace.
    val request = ws.url("https://www.toggl.com/api/v8/workspaces/xxxx/projects")
      .withHeaders("Content-Type" -> "application/Json")
      .withAuth(apiToken, "api_token", BASIC)

    request.get().map { response =>
      val bodyJSValue: JsValue = Json.parse(response.body)
      val validateBody = bodyJSValue.validate[List[Project]]
      ws.close()
      validateBody match {
        case JsSuccess(projectList: List[Project], _) => projectList.map(_.name)
        case JsError(_) => List.empty
      }
    }
  }
}
