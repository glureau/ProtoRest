package com.glureau.compiler.test.todo

import com.glureau.compiler.test.api.GithubApiService
import com.glureau.compiler.test.api.dto.GithubOrganization
import com.glureau.compiler.test.api.dto.data.GithubOrganizationDao
import com.glureau.compiler.test.api.dto.data.GithubOrganizationEntity
import com.glureau.geno.lib.network.CacheManager
import com.glureau.geno.lib.repository.NetworkBoundResource
import io.reactivex.Maybe

class GithubOrganizationRepository(val networkApi: GithubApiService,
                                   val githubOrganizationDao: GithubOrganizationDao,
                                   val organizationsCacheManager: CacheManager<GithubOrganizationEntity>) {

    fun getAllOrganizations() = object : NetworkBoundResource<List<GithubOrganization>, List<GithubOrganizationEntity>, List<GithubOrganization>>() {
        override fun saveRemoteCallResult(entity: List<GithubOrganizationEntity>) {
            entity.forEach {
                githubOrganizationDao.insertGithubOrganization(it)
            }
        }

        override fun shouldFetch(entity: List<GithubOrganizationEntity>?): Boolean {
            return organizationsCacheManager.shouldFetch(entity)
        }

        override fun entityToDomain(entity: List<GithubOrganizationEntity>?) = entity?.map { it.toDomain() }

        override fun domainToEntity(domain: List<GithubOrganization>) = domain.map { it.toEntity() }

        override fun dtoToDomain(dto: List<GithubOrganization>) = dto

        override fun loadFromDb(): Maybe<List<GithubOrganizationEntity>> {
            return githubOrganizationDao.getGithubOrganizations()
        }

        override fun createRemoteCall() = networkApi.getAllOrganizations()
    }.asLiveData()
}

fun GithubOrganization.toEntity() = GithubOrganizationEntity(
        id = id,
        login = login,
        description = description,
        url = url,
        avatar_url = avatar_url,
        events_url = events_url,
        members_url = members_url,
        hooks_url = url,
        issues_url = issues_url,
        public_members_url = public_members_url,
        repos_url = repos_url
)

fun GithubOrganizationEntity.toDomain() = GithubOrganization(
        id = id,
        login = login,
        description = description,
        url = url,
        avatar_url = avatar_url,
        events_url = events_url,
        members_url = members_url,
        hooks_url = url,
        issues_url = issues_url,
        public_members_url = public_members_url,
        repos_url = repos_url
)
