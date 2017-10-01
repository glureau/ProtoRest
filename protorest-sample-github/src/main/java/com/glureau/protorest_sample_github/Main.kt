package com.glureau.protorest_sample_github

import com.glureau.protorest_core.ProtoRestApplication
import com.glureau.protorest_core.RestApi
import com.glureau.protorest_core.RestFeature
import java.util.*

class MainApplication : ProtoRestApplication<GithubUserApi>(title = "Github-API", api = GithubUserApi()) {
    init {
        setup = listOf(
                RestFeature("User / glureau (full)", { api.userGlureau() }, { a, f, r -> this.generateViews(a, f, r) }),
                RestFeature("User / glureau (simple)", { api.userSimpleGlureau() }, { a, f, r -> this.generateViews(a, f, r) }),
                RestFeature("User / jakewharton (simple)", { api.userSimpleJakeWharton() }, { a, f, r -> this.generateViews(a, f, r) }),
                RestFeature("User / swankjesse (simple)", { api.userSimpleSwankJesse() }, { a, f, r -> this.generateViews(a, f, r) })
        )
    }
}

data class SimpleGithubUser(val login: String, val avatar_url: String?, val url: String?, val name: String?, val company: String?, val email: String?, val followers: Int, val following: Int)
data class GithubUser(val login: String, val id: Long, val avatar_url: String?, val gravatar_id: String?, val url: String?, val html_url: String?, val followers_url: String?, val following_url: String?, val gists_url: String?, val starred_url: String?, val subscriptions_url: String?, val organizations_url: String?, val repos_url: String?, val events_url: String?, val received_events_url: String?, val type: String?, val site_admin: Boolean?, val name: String?, val company: String?, val blog: String?, val location: String?, val email: String?, val hireable: String?, val bio: String?, val public_repos: Int, val public_gists: Int, val followers: Int, val following: Int, val created_at: Date, val updated_at: Date)
class GithubUserApi : RestApi("https://api.github.com/") {
    fun userGlureau() = user("glureau")
    fun userSimpleGlureau() = userSimple("glureau")
    fun userSimpleJakeWharton() = userSimple("jakewharton")
    fun userSimpleSwankJesse() = userSimple("swankjesse")
    fun user(login: String) = rest("users/$login", GithubUser::class.java)
    fun userSimple(login: String) = rest("users/$login", SimpleGithubUser::class.java)
}
