package com.glureau.compiler.test.api

import com.glureau.compiler.test.api.dto.SimpleGithubOrganization
import com.glureau.compiler.test.api.dto.SimpleGithubUser
import com.glureau.geno.annotation.data.ManyToMany

@ManyToMany(SimpleGithubOrganization::class, SimpleGithubUser::class)
object Mapping// Here only to support mapping annotations
