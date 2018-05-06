package com.glureau.compiler.test.todo

import com.glureau.compiler.test.api.GithubApiService
import com.glureau.compiler.test.model.SimpleGithubUser
import com.glureau.compiler.test.model.data.SimpleGithubUserDao
import com.glureau.compiler.test.model.data.SimpleGithubUserEntity
import com.glureau.geno.lib.network.CacheManager
import com.glureau.geno.lib.repository.NetworkBoundResource
import io.reactivex.Maybe

class GithubUserRepository(val networkApi: GithubApiService, val dao: SimpleGithubUserDao, val cacheManager: CacheManager<SimpleGithubUserEntity>) {

    fun getMembers(org: String) = object : NetworkBoundResource<List<SimpleGithubUser>, List<SimpleGithubUserEntity>, List<SimpleGithubUser>>() {
        override fun saveRemoteCallResult(entity: List<SimpleGithubUserEntity>) {
            entity.forEach {
                dao.insertSimpleGithubUser(it)
            }
        }

        override fun shouldFetch(entity: List<SimpleGithubUserEntity>?): Boolean {
            return cacheManager.shouldFetch(entity)
        }

        override fun entityToDomain(entity: List<SimpleGithubUserEntity>?) = entity?.map { it.toDomain() }

        override fun domainToEntity(domain: List<SimpleGithubUser>) = domain.map { it.toEntity() }

        override fun dtoToDomain(dto: List<SimpleGithubUser>) = dto

        override fun loadFromDb(): Maybe<List<SimpleGithubUserEntity>> {
            return dao.getSimpleGithubUsers()
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
