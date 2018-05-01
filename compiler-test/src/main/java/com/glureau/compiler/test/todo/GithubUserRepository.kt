package com.glureau.compiler.test.todo

import com.glureau.compiler.test.api.GithubApiService
import com.glureau.compiler.test.model.SimpleGithubUser
import com.glureau.compiler.test.model.data.SimpleGithubUserDao
import com.glureau.compiler.test.model.data.SimpleGithubUserEntity
import com.glureau.geno.lib.network.CacheManager
import com.glureau.geno.lib.repository.NetworkBoundResource
import io.reactivex.Maybe

class GithubUserRepository(val networkApi: GithubApiService, val dao: SimpleGithubUserDao, val cacheManager: CacheManager<SimpleGithubUser>) {

    fun getMembers(org: String) = object : NetworkBoundResource<List<SimpleGithubUser>>() {
        override fun saveRemoteCallResult(item: List<SimpleGithubUser>) {
            item.forEach {
                dao.insertSimpleGithubUser(it.toEntity())
            }
        }

        override fun shouldFetch(data: List<SimpleGithubUser>?): Boolean {
            return cacheManager.shouldFetch(data)
        }

        override fun loadFromDb(): Maybe<List<SimpleGithubUser>> {
            return dao.getSimpleGithubUsers()
                    .map { entities -> entities.map { it.toDomain() } }
        }

        override fun createRemoteCall() = networkApi.getMembers(org)
    }.asLiveData()
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
