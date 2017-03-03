package services

import play.api.libs.json.{ Format, Json }
import scala.concurrent.Future

/**
 * This is the interface of the GitHub service which returns the list of repositories that a user can
 * access in Github.
 */
trait GitHubService {

  /**
   * Get a list of projects for a certain Github account
   * @param oauthToken Authentication token which is received from Github
   * @return a Future List of Github repositories
   */
  def getGitHubProjects(oauthToken: String): Future[List[String]]
}

/**
 * Companion object including case class model for GitHub related entities (Owner, Permission, Repo),
 * and Reads/Writes to convert them from/to json format.
 */
object GitHubService {

  /**
   * Model case class for the github repository owner
   * @param login
   * @param id
   * @param avatar_url
   * @param gravatar_id
   * @param url
   * @param html_url
   * @param followers_url
   * @param following_url
   * @param gists_url
   * @param starred_url
   * @param subscriptions_url
   * @param organizations_url
   * @param repos_url
   * @param events_url
   * @param received_events_url
   * @param `type`
   * @param site_admin
   */
  case class Owner(
    login: String,
    id: Int,
    avatar_url: String,
    gravatar_id: String,
    url: String,
    html_url: String,
    followers_url: String,
    following_url: String,
    gists_url: String,
    starred_url: String,
    subscriptions_url: String,
    organizations_url: String,
    repos_url: String,
    events_url: String,
    received_events_url: String,
    `type`: String,
    site_admin: Boolean
  )

  /**
   * Model case class for the permissions applied on a certain Github repository
   * @param admin
   * @param push
   * @param pull
   */
  case class Permission(admin: Boolean, push: Boolean, pull: Boolean)

  /**
   * Model case class for a github repository
   * @param id
   * @param name
   * @param full_name
   * @param owner
   * @param `private`
   * @param html_url
   * @param description
   * @param fork
   * @param url
   * @param forks_url
   * @param keys_url
   * @param collaborators_url
   * @param teams_url
   * @param hooks_url
   * @param issue_events_url
   * @param events_url
   * @param assignees_url
   * @param branches_url
   * @param tags_url
   * @param blobs_url
   * @param permissions
   */
  case class Repo(
    id: Int,
    name: String,
    full_name: String,
    owner: Owner,
    `private`: Boolean,
    html_url: String,
    description: Option[String],
    fork: Boolean,
    url: String,
    forks_url: String,
    keys_url: String,
    collaborators_url: String,
    teams_url: String,
    hooks_url: String,
    issue_events_url: String,
    events_url: String,
    assignees_url: String,
    branches_url: String,
    tags_url: String,
    blobs_url: String,
    permissions: Permission
  )

  implicit val ownerFormat: Format[Owner] = Json.format[Owner]
  implicit val permissionFormat: Format[Permission] = Json.format[Permission]
  implicit val repoFormat: Format[Repo] = Json.format[Repo]
}
