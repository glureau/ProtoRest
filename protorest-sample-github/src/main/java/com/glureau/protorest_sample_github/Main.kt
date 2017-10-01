package com.glureau.protorest_sample_github

import com.glureau.protorest_core.ProtoRestApplication
import com.glureau.protorest_core.RestApi
import com.glureau.protorest_core.RestFeature

class MainApplication : ProtoRestApplication<GithubUserApi>(title = "Github-API", api = GithubUserApi()) {
    init {
        setup = listOf(
                RestFeature("User", { api.userGlureau() }, {a, f, r -> this.generateViews(a, f, r)})
        )
    }
}

data class GithubUser(val login: String, val id: Long, val avatar_url: String)
class GithubUserApi : RestApi("https://api.github.com/") {
    fun userGlureau() = user("glureau")
    fun user(login: String) = rest("users/$login", GithubUser::class.java)
}



