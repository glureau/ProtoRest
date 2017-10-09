package com.glureau.protorest_sample_github

import com.glureau.protorest_core.ProtoRestApplication
import com.glureau.protorest_core.rest.RestApi
import com.glureau.protorest_core.rest.annotation.*
import java.util.*

// Define your entry point by filling the api to the ProtoRestApplication
class MainApplication : ProtoRestApplication<GithubUserApi>(api = GithubUserApi())

// Pick only interesting fields
@CustomView(R.layout.user)
data class SimpleGithubUser(
        val login: String?,
        val name: String?, @Image val avatar_url: String?,
        val created_at: Date?, val url: String?, val company: String?, val location: String?,
        val followers: Int?, val following: Int?, val public_repos: Int?, val public_gists: Int?)

data class SimpleGithubOrganization(val url: String?, val repos_url: String?, @Image val avatar_url: String?, val description: String?, val name: String?, val html_url: String?)

data class SimpleGithubRepository(val name: String, val owner: SimpleGithubUser?, val html_url: String?, val description: String?, val fork: Boolean)

data class GithubError(val message: String, val documentation_url: String?)

typealias SimpleGithubRepositoryList = Array<SimpleGithubRepository>

@RestError(GithubError::class)
class GithubUserApi : RestApi("https://api.github.com/") {
    @Endpoint(SimpleGithubUser::class) fun userSimple(
            @EndpointParam(name = "login", defaultValue = "glureau", suggestedValues = arrayOf("glureau", "jakewharton", "swankjesse", "pyricau", "alecholmes", "akarnokd")) login: String)
            = get("users/$login", SimpleGithubUser::class.java)

    @Endpoint(SimpleGithubOrganization::class) fun organizationSimple(
            @EndpointParam(name = "org", suggestedValues = arrayOf("github", "google", "square", "apple", "microsoft")) org: String)
            = get("orgs/$org", SimpleGithubOrganization::class.java)

    @Endpoint(SimpleGithubRepository::class) fun repositorySimple(
            @EndpointParam(name = "login", defaultValue = "glureau") login: String,
            @EndpointParam(name = "repos", defaultValue = "protorest") repos: String)
            = get("repos/$login/$repos", SimpleGithubRepository::class.java)

    @Endpoint(SimpleGithubRepositoryList::class) fun userRepository(
            @EndpointParam(name = "login", defaultValue = "glureau", suggestedValues = arrayOf("glureau", "jakewharton", "swankjesse", "pyricau", "alecholmes", "akarnokd")) login: String)
            = get("users/$login/repos?affiliation=owner", SimpleGithubRepositoryList::class.java)
}


// References
//data class GithubUser(val login: String?, val id: Long?, @Image val avatar_url: String?, val gravatar_id: String?, val url: String?, val html_url: String?, val followers_url: String?, val following_url: String?, val gists_url: String?, val starred_url: String?, val subscriptions_url: String?, val organizations_url: String?, val repos_url: String?, val events_url: String?, val received_events_url: String?, val type: String?, val site_admin: Boolean?, val name: String?, val company: String?, val blog: String?, val location: String?, val email: String?, val hireable: String?, val bio: String?, val public_repos: Int?, val public_gists: Int?, val followers: Int?, val following: Int?, val created_at: Date?, val updated_at: Date?)
//data class GithubOrganization(val login: String?, val id: Long?, val url: String?, val repos_url: String?, val events_url: String?, val hooks_url: String?, val issues_url: String?, val members_url: String?, val public_members_url: String?, @Image val avatar_url: String?, val description: String?, val name: String?, val company: String?, val blog: String?, val location: String?, val email: String?, val has_organization_projects: Boolean?, val has_repository_projects: Boolean?, val public_repos: Int?, val public_gists: Int?, val followers: Int?, val following: Int?, val html_url: String?, val created_at: Date?, val updated_at: Date?, val type: String?)
//data class GithubRepository(val id: Long?, val name: String?, val full_name: String?, val owner: GithubUser?, val private: Boolean?, val html_url: String?, val description: String?, val fork: Boolean?, val url: String?, val forks_url: String?, val keys_url: String?, val collaborators_url: String?, val teams_url: String?, val hooks_url: String?, val issue_events_url: String?, val events_url: String?, val assignees_url: String?, val branches_url: String?, val tags_url: String?, val blobs_url: String?, val git_tags_url: String?, val git_refs_url: String?, val trees_url: String?, val statuses_url: String?, val languages_url: String?, val stargazers_url: String?, val contributors_url: String?, val subscribers_url: String?, val subscription_url: String?, val commits_url: String?, val git_commits_url: String?, val comments_url: String?, val issue_comment_url: String?, val contents_url: String?, val compare_url: String?, val merges_url: String?, val archive_url: String?, val downloads_url: String?, val issues_url: String?, val pulls_url: String?, val milestones_url: String?, val notifications_url: String?, val labels_url: String?, val releases_url: String?, val deployments_url: String?, val created_at: Date?, val updated_at: Date?, val pushed_at: Date?, val git_url: String?, val ssh_url: String?, val clone_url: String?, val svn_url: String?, val homepage: String?, val size: Int?, val stargazers_count: Int?, val watchers_count: Int?, val language: String?, val has_issues: Boolean?, val has_projects: Boolean?, val has_downloads: Boolean?, val has_wiki: Boolean?, val has_pages: Boolean?, val forks_count: Int?, val mirror_url: String?, val open_issues_count: Int?, val forks: Int?, val open_issues: Int?, val watchers: Int?, val default_branch: String?, val network_count: Int?, val subscribers_count: Int?)
