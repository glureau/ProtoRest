package com.glureau.protorest_sample_github

import com.glureau.protorest_core.ProtoRestApplication
import com.glureau.protorest_core.RestApi
import java.nio.file.Path

class MainApplication : ProtoRestApplication<GithubUserApi>(api = GithubUserApi()) {
    init {
        setup = mapOf(
                "User" to { api.userGlureau() }
        )
    }
}

class GithubUserApi : RestApi("https://api.github.com/") {
    fun userGlureau() = user("glureau")
    fun user(login: String) = rest("users/$login", mapOf(
            "login" to String::class,
            "id" to Long::class,
            "avatar_url" to Path::class
    ))
}

