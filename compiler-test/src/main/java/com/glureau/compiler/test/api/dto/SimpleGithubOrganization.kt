package com.glureau.compiler.test.api.dto

import com.glureau.geno.annotation.data.Identifier
import com.glureau.geno.annotation.storage.ViewModel
import com.glureau.geno.annotation.view.CustomView

@CustomView(com.glureau.test.R::class, "simple_organization")
@ViewModel
open class SimpleGithubOrganization(@Identifier open val login: String,
                                    open val description: String?,
                                    open val avatar_url: String?,
                                    open val url: String?,
                                    open val repos_url: String,
                                    open val events_url: String,
                                    open val hooks_url: String,
                                    open val issues_url: String,
                                    open val members_url: String,
                                    open val public_members_url: String)