package com.glureau.compiler.test.todo

import android.arch.lifecycle.LiveData
import com.glureau.compiler.test.api.GithubApiService
import com.glureau.compiler.test.model.SimpleGithubUser
import com.glureau.compiler.test.model.data.SimpleGithubUserDao
import com.glureau.compiler.test.model.data.SimpleGithubUserEntity
import com.glureau.geno.lib.repository.NetworkBoundResource
import com.glureau.geno.lib.repository.Resource
import io.reactivex.Maybe

class GithubUserRepository(val networkApi: GithubApiService, val dao: SimpleGithubUserDao) {

    fun getMembers(org: String): LiveData<Resource<List<SimpleGithubUser>>> {
        val nbr = object : NetworkBoundResource<List<SimpleGithubUser>>() {
            override fun saveRemoteCallResult(item: List<SimpleGithubUser>) {
                item.forEach {
                    dao.insertSimpleGithubUser(it.toEntity())
                }
            }

            override fun shouldFetch(data: List<SimpleGithubUser>?): Boolean {
                return true // infinite cache
            }

            override fun loadFromDb(): Maybe<List<SimpleGithubUser>> {
                return dao.getSimpleGithubUsers()
                        .map { entities -> entities.map { it.toDomain() } }
            }

            override fun createRemoteCall() = networkApi.getMembers(org)
        }
        return nbr.asLiveData()
    }
}

fun SimpleGithubUser.toEntity() = SimpleGithubUserEntity(
        id = id,
        login = login,
        avatar_url = avatar_url,
        html_url = html_url
)

fun SimpleGithubUserEntity.toDomain() = SimpleGithubUser(
        id = id,
        login = login,
        avatar_url = avatar_url,
        html_url = html_url
)
