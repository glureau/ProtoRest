package com.glureau.compiler.test.todo

import com.glureau.compiler.test.api.GithubApiService
import com.glureau.compiler.test.api.dto.SimpleGithubUser
import com.glureau.compiler.test.api.dto.data.SimpleGithubOrganization_to_SimpleGithubUser
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
                simpleGithubUserDao.insertSimpleGithubUser(it)//TODO: coupling with the org
                simpleGithubUserDao.insertSimpleGithubOrganization_to_SimpleGithubUser(SimpleGithubOrganization_to_SimpleGithubUser(SimpleGithubOrganization_id = org ))
            }
        }

        override fun shouldFetch(entity: List<SimpleGithubUserEntity>?): Boolean {
            return simpleUserCacheManager.shouldFetch(entity)
        }

        override fun entityToDomain(entity: List<SimpleGithubUserEntity>?) = entity?.map { it.toDomain() }

        override fun domainToEntity(domain: List<SimpleGithubUser>) = domain.map { it.toEntity() }

        override fun dtoToDomain(dto: List<SimpleGithubUser>) = dto

        override fun loadFromDb(): Maybe<List<SimpleGithubUserEntity>> {
            return simpleGithubUserDao.getSimpleGithubUserByOrgs(org)//TODO: fetching users based on the org
        }

        override fun createRemoteCall() = networkApi.getMembers(org)
    }.asLiveData()

}

fun SimpleGithubUser.toEntity() = SimpleGithubUserEntity(
        login = login,
        avatar_url = avatar_url,
        html_url = html_url
)

fun SimpleGithubUserEntity.toDomain() = SimpleGithubUser(
        login = login,
        avatar_url = avatar_url,
        html_url = html_url
)