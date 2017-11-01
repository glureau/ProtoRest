package com.glureau.compiler.test.model

import com.glureau.geno.annotation.CustomView
import com.glureau.geno.annotation.Image
import java.util.Date

// Minimal view, used for lists
@CustomView(com.glureau.test.R::class, "simple_user")
open class SimpleGithubUser (open val id: Long, open val login: String, @Image open val avatar_url: String?, open val html_url: String?)

// Complete view
@CustomView(com.glureau.test.R::class, "user")
data class GithubUser(
        override val id: Long, override val login: String, override val avatar_url: String?, override val html_url: String?,
        val name: String?,
        val created_at: Date?, val company: String?, val location: String?,
        val followers: Int?, val following: Int?, val public_repos: Int?, val public_gists: Int?) : SimpleGithubUser(id, login, avatar_url, html_url)

