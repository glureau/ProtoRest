package com.glureau.compiler.test.todo

import com.glureau.compiler.test.api.GithubApiService
import com.glureau.compiler.test.api.dto.SimpleGithubUser
import com.glureau.compiler.test.api.dto.data.SimpleGithubUserDao
import com.glureau.compiler.test.api.dto.data.SimpleGithubUserEntity
import com.glureau.geno.lib.network.CacheManager
import com.glureau.geno.lib.repository.NetworkBoundResource
import io.reactivex.Maybe

class GithubUserRepository(val networkApi: GithubApiService,
                           val simpleGithubUserDao: SimpleGithubUserDao,
                           val simpleUserCacheManager: CacheManager<SimpleGithubUserEntity>) {

    fun getMembers(org: String) = object : NetworkBoundResource<List<SimpleGithubUser>, List<SimpleGithubUserEntity>, List<SimpleGithubUser>>() {
        override fun saveRemoteCallResult(entity: List<SimpleGithubUserEntity>) {
            entity.forEach {
                simpleGithubUserDao.insertSimpleGithubUser(it)
            }
        }

        override fun shouldFetch(entity: List<SimpleGithubUserEntity>?): Boolean {
            return simpleUserCacheManager.shouldFetch(entity)
        }

        override fun entityToDomain(entity: List<SimpleGithubUserEntity>?) = entity?.map { it.toDomain() }

        override fun domainToEntity(domain: List<SimpleGithubUser>) = domain.map { it.toEntity() }

        override fun dtoToDomain(dto: List<SimpleGithubUser>) = dto

        override fun loadFromDb(): Maybe<List<SimpleGithubUserEntity>> {
            return simpleGithubUserDao.getSimpleGithubUsers()
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