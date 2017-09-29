package com.glureau.protorest_sample_github

import com.glureau.protorest_core.ProtoRestApplication
import com.glureau.protorest_core.RestApi
import com.glureau.protorest_core.RestResult
import java.nio.file.Path

class MainApplication : ProtoRestApplication(
        api = GithubUserApi()
)

class GithubUserApi : RestApi("https://api.github.com/") {
    class GithubUser(val login: String, val id: Long, val avatar_url: Path) : RestResult
    fun user(login: String) = rest("users/$login", GithubUser::class)
}

