package com.glureau.protorest_sample_github

import android.util.Log
import com.glureau.protorest_core.ProtoRestApplication
import com.glureau.protorest_core.RestApi
import java.util.*

class MainApplication : ProtoRestApplication<GithubUserApi>(title = "Github-API", api = GithubUserApi()) {
    init {
        setup(
                group("User",
                        feature("glureau (full)", { api.user("glureau")}),
                        feature("glureau (simple)", { api.userSimple("glureau") }),
                        feature("jakewharton (simple)", { api.userSimple("jakewharton") }),
                        feature("swankjesse (simple)", { api.userSimple("swankjesse") })),
                group("Organization",
                        feature("square (full)", { api.organization("square") }),
                        feature("square (simple)", { api.organizationSimple("square") }),
                        feature("betomorrow (simple)", { api.organizationSimple("betomorrow") })
                ),
                group("Repos",
                        feature("ProtoRest (full)", { api.repository("glureau", "protorest") }),
                        feature("ProtoRest (simple)", { api.repositorySimple("glureau", "protorest") })
                )
        )
    }
}

// This is just an example based on a couple of result.
// This is NOT a clean github implementation.
data class SimpleGithubUser(val login: String?, @RestApi.Image val avatar_url: String?, val url: String?, val name: String?, val company: String?, val email: String?, val followers: Int?, val following: Int?)
data class GithubUser(val login: String?, val id: Long?, val avatar_url: String?, val gravatar_id: String?, val url: String?, val html_url: String?, val followers_url: String?, val following_url: String?, val gists_url: String?, val starred_url: String?, val subscriptions_url: String?, val organizations_url: String?, val repos_url: String?, val events_url: String?, val received_events_url: String?, val type: String?, val site_admin: Boolean?, val name: String?, val company: String?, val blog: String?, val location: String?, val email: String?, val hireable: String?, val bio: String?, val public_repos: Int?, val public_gists: Int?, val followers: Int?, val following: Int?, val created_at: Date?, val updated_at: Date?)
data class SimpleGithubOrganization(val url: String?, val repos_url: String?, val avatar_url: String?, val description: String?, val name: String?, val html_url: String?)
data class GithubOrganization(val login: String?, val id: Long?, val url: String?, val repos_url: String?, val events_url: String?, val hooks_url: String?, val issues_url: String?, val members_url: String?, val public_members_url: String?, val avatar_url: String?, val description: String?, val name: String?, val company: String?, val blog: String?, val location: String?, val email: String?, val has_organization_projects: Boolean?, val has_repository_projects: Boolean?, val public_repos: Int?, val public_gists: Int?, val followers: Int?, val following: Int?, val html_url: String?, val created_at: Date?, val updated_at: Date?, val type: String?)
data class SimpleGithubRepository(val full_name: String?, val owner: SimpleGithubUser?, val private: Boolean?, val html_url: String?, val description: String?)
data class GithubRepository(val id: Long?, val name: String?, val full_name: String?, val owner: GithubUser?, val private: Boolean?, val html_url: String?, val description: String?, val fork: Boolean?, val url: String?, val forks_url: String?, val keys_url: String?, val collaborators_url: String?, val teams_url: String?, val hooks_url: String?, val issue_events_url: String?, val events_url: String?, val assignees_url: String?, val branches_url: String?, val tags_url: String?, val blobs_url: String?, val git_tags_url: String?, val git_refs_url: String?, val trees_url: String?, val statuses_url: String?, val languages_url: String?, val stargazers_url: String?, val contributors_url: String?, val subscribers_url: String?, val subscription_url: String?, val commits_url: String?, val git_commits_url: String?, val comments_url: String?, val issue_comment_url: String?, val contents_url: String?, val compare_url: String?, val merges_url: String?, val archive_url: String?, val downloads_url: String?, val issues_url: String?, val pulls_url: String?, val milestones_url: String?, val notifications_url: String?, val labels_url: String?, val releases_url: String?, val deployments_url: String?, val created_at: Date?, val updated_at: Date?, val pushed_at: Date?, val git_url: String?, val ssh_url: String?, val clone_url: String?, val svn_url: String?, val homepage: String?, val size: Int?, val stargazers_count: Int?, val watchers_count: Int?, val language: String?, val has_issues: Boolean?, val has_projects: Boolean?, val has_downloads: Boolean?, val has_wiki: Boolean?, val has_pages: Boolean?, val forks_count: Int?, val mirror_url: String?, val open_issues_count: Int?, val forks: Int?, val open_issues: Int?, val watchers: Int?, val default_branch: String?, val network_count: Int?, val subscribers_count: Int?)

class GithubUserApi : RestApi("https://api.github.com/") {
    fun user(login: String) = get("users/$login", GithubUser::class.java)
    fun userSimple(login: String) = get("users/$login", SimpleGithubUser::class.java)
    fun organization(orga: String) = get("orgs/$orga", GithubOrganization::class.java)
    fun organizationSimple(orga: String) = get("orgs/$orga", SimpleGithubOrganization::class.java)
    fun repository(login: String, repos: String) = get("repos/$login/$repos", GithubRepository::class.java)
    fun repositorySimple(login: String, repos: String) = get("repos/$login/$repos", SimpleGithubRepository::class.java)
}
