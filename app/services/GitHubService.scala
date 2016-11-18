package services

import play.api.libs.json.{Format, Json}
import scala.concurrent.Future

/**
  * This is the interface of the GitHub service which is supposed to reture the list of projects that a user can
  * access in github.
  */
trait GitHubService {

  /**
    * Get a list of projects for a certain github account
    * @param oauthToken
    * @return a Future List of github repositories
    */
  def getGitHubProjects(oauthToken: Option[String]): Future[List[String]]
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
                    `type`:String,
                    site_admin:Boolean
                  )

  /**
    * Model case class for the permissions applied on a certain github repository
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
                   name:String,
                   full_name:String,
                   owner: Owner,
                   `private` : Boolean,
                   html_url :String,
                   description: Option[String],
                   fork: Boolean,
                   url: String,
                   forks_url: String,
                   keys_url: String,
                   collaborators_url:String,
                   teams_url:String,
                   hooks_url:String,
                   issue_events_url:String,
                   events_url:String,
                   assignees_url:String,
                   branches_url:String,
                   tags_url:String,
                   blobs_url:String,
                   //git_tags_url:String,
                   //git_refs_url:String,
                   //trees_url:String,
                   //statuses_url:String,
                   //languages_url:String,
                   //stargazers_url:String,
                   //contributors_url:String,
                   //subscribers_url:String,
                   //subscription_url:String,
                   //commits_url:String,
                   //git_commits_url:String,
                   //comments_url:String,
                   //issue_comment_url:String,
                   //contents_url:String,
                   //compare_url:String,
                   //merges_url:String,
                   //archive_url:String,
                   //downloads_url:String,
                   //issues_url:String,
                   //pulls_url:String,
                   //milestones_url:String,
                   //notifications_url:String,
                   //labels_url:String,
                   //releases_url:String,
                   //deployments_url:String,
                   //created_at:String,
                   //updated_at:String,
                   //pushed_at:String,
                   //git_url:String,
                   //ssh_url:String,
                   //clone_url:String,
                   //svn_url:String,
                   //homepage:String,
                   //size:Int,
                   //stargazers_count:Int,
                   //watchers_count:Int,
                   //language:String,
                   //has_issues: Boolean,
                   //has_downloads:Boolean,
                   //has_wiki:Boolean,
                   //has_pages:Boolean,
                   //forks_count:Int,
                   //mirror_url:String,
                   //open_issues_count:Int,
                   //forks:Int,
                   //open_issues:Int,
                   //watchers:Int,
                   //default_branch:String,
                   permissions: Permission
                 )

  implicit val ownerFormat: Format[Owner] = Json.format[Owner]
  implicit val permissionFormat: Format[Permission] = Json.format[Permission]
  implicit val repoFormat: Format[Repo] = Json.format[Repo]
}