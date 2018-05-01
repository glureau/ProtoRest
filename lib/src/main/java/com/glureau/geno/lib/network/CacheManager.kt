package com.glureau.geno.lib.network

import com.glureau.geno.annotation.db.InternalEntity
import java.util.*

interface CacheManager<in T : InternalEntity> {
    fun useCacheDatabase(): Boolean
    fun shouldFetch(data: List<T>?): Boolean
}

class NoCacheManagerImpl<in T : InternalEntity> : CacheManager<T> {
    override fun useCacheDatabase() = false
    override fun shouldFetch(data: List<T>?) = true
}

/**
 * Fetch new data when the cache is too old.
 * @param cacheDuration in milliseconds
 */
class TimedCacheManagerImpl<in T : InternalEntity>(private val cacheDuration: Long) : CacheManager<T> {
    override fun useCacheDatabase() = true
    override fun shouldFetch(data: List<T>?): Boolean {
        if (data == null) return true
        data.forEach {
            val lastUpdateTime = it._internal_update_date.time
            if (lastUpdateTime + cacheDuration < Calendar.getInstance().time.time) return true
        }
        return false // All data is up-to-date
    }
}
