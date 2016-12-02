package services.impl

import services.TogglService
import services.TogglService.{Project, Workspace}
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.ws.WSAuthScheme.BASIC
import play.api.libs.json._

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
    //TODO: The workspace ID should be passed to this method.
    // Meanwhile, in order to test change the xxxx to a roper workspace.

    val workspace = getTogglWorkspace(apiToken)

    val request = ws.url(s"https://www.toggl.com/api/v8/workspaces/94268/projects")
      .withHeaders("Content-Type" -> "application/Json")
      .withAuth(apiToken, "api_token", BASIC)

    request.get().map { response =>
      val bodyJSValue: JsValue = Json.parse(response.body)
      val validateBody = bodyJSValue.validate[List[Project]]
      validateBody match {
        case JsSuccess(projectList: List[Project], _) => projectList.map(_.name)
        case JsError(_) => List.empty
      }
    }
  }

  override def getTogglWorkspace(apiToken: String): Future[List[String]] = {
    println("el primero - " + apiToken)
    val request = ws.url("https://www.toggl.com/api/v8/workspaces")
      .withHeaders("Content-Type" -> "application/Json")
      .withAuth(apiToken, "api_token", BASIC)

    println("el segundo - " + request.get())

    request.get().map { response =>
      println(s"Dentro response: ")
      println(s"${response.body}")
      val bodyJSValue: JsValue = Json.parse(response.body)
      val validateBody = bodyJSValue.validate[List[Workspace]]
      validateBody match {
        case JsSuccess(workspaceList: List[Workspace], _) =>
          println("Ben")
          workspaceList.map(_.name)
        case JsError(_) =>
          println("Bad")
          List.empty
        case _ =>
          println("Really bad")
          List.empty
      }
    }
  }
}
