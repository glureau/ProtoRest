package com.glureau.compiler.test.api.dto

import com.glureau.geno.annotation.Image
import com.glureau.geno.annotation.storage.ViewModel
import com.glureau.geno.annotation.view.CustomView

// Minimal view, used for lists
@CustomView(com.glureau.test.R::class, "simple_user")
@ViewModel
open class SimpleGithubUser(open val id: Long, open val login: String, @Image open val avatar_url: String?, open val html_url: String?)
