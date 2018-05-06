package com.glureau.compiler.test.api.dto

import com.glureau.geno.annotation.storage.ViewModel
import com.glureau.geno.annotation.view.CustomView

@CustomView(com.glureau.test.R::class, "organization")
@ViewModel
data class GithubOrganization(val id: Long,
                              val login: String,
                              val description: String?,
                              val avatar_url: String?,
                              val url: String,
                              val repos_url: String,
                              val events_url: String,
                              val hooks_url: String,
                              val issues_url: String,
                              val members_url: String,
                              val public_members_url: String)