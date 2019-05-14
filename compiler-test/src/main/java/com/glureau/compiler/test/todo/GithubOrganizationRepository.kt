package com.glureau.compiler.test.todo

import com.glureau.compiler.test.api.GithubApiService
import com.glureau.compiler.test.api.dto.SimpleGithubOrganization
import com.glureau.compiler.test.api.dto.data.SimpleGithubOrganizationDao
import com.glureau.compiler.test.api.dto.data.SimpleGithubOrganizationEntity
import com.glureau.geno.lib.network.CacheManager
import com.glureau.geno.lib.repository.NetworkBoundResource
import io.reactivex.Maybe

class GithubOrganizationRepository(val networkApi: GithubApiService,
                                   val githubOrganizationDao: SimpleGithubOrganizationDao,
                                   val organizationsCacheManager: CacheManager<SimpleGithubOrganizationEntity>) {

    fun getAllOrganizations() = object : NetworkBoundResource<List<SimpleGithubOrganization>, List<SimpleGithubOrganizationEntity>, List<SimpleGithubOrganization>>() {
        override fun saveRemoteCallResult(entity: List<SimpleGithubOrganizationEntity>) {
            entity.forEach {
                githubOrganizationDao.insertSimpleGithubOrganization(it)
            }
        }

        override fun shouldFetch(entity: List<SimpleGithubOrganizationEntity>?): Boolean {
            return organizationsCacheManager.shouldFetch(entity)
        }

        override fun entityToDomain(entity: List<SimpleGithubOrganizationEntity>?) = entity?.map { it.toDomain() }

        override fun domainToEntity(domain: List<SimpleGithubOrganization>) = domain.map { it.toEntity() }

        override fun dtoToDomain(dto: List<SimpleGithubOrganization>) = dto

        override fun loadFromDb(): Maybe<List<SimpleGithubOrganizationEntity>> {
            return githubOrganizationDao.getSimpleGithubOrganizations()
        }

        override fun createRemoteCall() = networkApi.getAllOrganizations()
    }.asLiveData()
}

fun SimpleGithubOrganization.toEntity() = SimpleGithubOrganizationEntity(
        login = login,
        description = description,
        url = url,
        avatar_url = avatar_url,
        events_url = events_url,
        members_url = members_url,
        hooks_url = hooks_url,
        issues_url = issues_url,
        public_members_url = public_members_url,
        repos_url = repos_url
)

fun SimpleGithubOrganizationEntity.toDomain() = SimpleGithubOrganization(
        login = login,
        description = description,
        url = url,
        avatar_url = avatar_url,
        events_url = events_url,
        members_url = members_url,
        hooks_url = hooks_url,
        issues_url = issues_url,
        public_members_url = public_members_url,
        repos_url = repos_url
)
