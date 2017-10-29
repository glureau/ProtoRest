package com.glureau.compiler.test.model

import com.glureau.geno.annotation.CustomView
import com.glureau.geno.annotation.Image
import java.util.*

@CustomView(com.glureau.test.R::class, "layout_user")
data class GithubUser(
        val name: String?, @Image val avatar_url: String?,
        val created_at: Date?, val html_url: String?, val company: String?, val location: String?,
        val followers: Int?, val following: Int?, val public_repos: Int?, val public_gists: Int?)

