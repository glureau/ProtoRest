package com.glureau.compiler.test.api

import com.glureau.compiler.test.api.dto.GithubOrganization
import com.glureau.compiler.test.api.dto.SimpleGithubOrganization
import com.glureau.compiler.test.api.dto.SimpleGithubUser
import com.glureau.geno.annotation.data.ReferenceTo
import com.glureau.geno.annotation.network.RestApi
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Greg on 03/11/2017.
 */
@RestApi
interface GithubApiService {
    @GET("organizations")
    fun getAllOrganizations(): Maybe<List<SimpleGithubOrganization>>

    @GET("orgs/{org}")
    fun getOrganizationDetails(@Path("org") @ReferenceTo(SimpleGithubOrganization::class, "login") org: String): Maybe<List<GithubOrganization>>

    @GET("orgs/{org}/members?per_page=100")
    fun getMembers(@Path("org") @ReferenceTo(SimpleGithubOrganization::class, "login") org: String): Maybe<List<SimpleGithubUser>>
}
