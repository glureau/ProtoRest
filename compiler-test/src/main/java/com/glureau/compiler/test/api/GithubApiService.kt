package com.glureau.compiler.test.api

import com.glureau.compiler.test.model.SimpleGithubUser
import com.glureau.geno.annotation.network.RestApi
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Greg on 03/11/2017.
 */
@RestApi
interface GithubApiService {
    @GET("orgs/{org}/members?per_page=1000")
    fun getMembers(@Path("org") org: String): Maybe<List<SimpleGithubUser>>
}
