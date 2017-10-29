package com.glureau.compiler.test.model

import com.glureau.geno.annotation.CustomView
import com.glureau.geno.annotation.Image

@CustomView(com.glureau.test.R::class, "simple_user")
data class SimpleGithubUser(val login: String?, @Image val avatar_url: String?, val html_url: String?)
