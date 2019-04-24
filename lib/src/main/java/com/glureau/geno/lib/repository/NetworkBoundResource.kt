package com.glureau.geno.lib.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.glureau.geno.lib.rx.DisposableManager
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*


/**
 * A RX generic class that can provide a resource backed by both the sqlite database and the network.
 * You can read more about it in the [Architecture Guide](https://developer.android.com/arch).
 * @param <ResultType>
 */
abstract class NetworkBoundResource<Domain, Entity, Dto> @MainThread protected constructor() : DisposableManager() {

    private val result = MutableLiveData<Resource<Domain>>()

    init {
        // Android developer guide could suggest sending an immediate loading status with null data...
        // Removed as it doesn't look interesting in our use cases.
        //result.value = Resource.loading(null)
        startDatabaseLoading()
    }

    @MainThread
    protected fun startDatabaseLoading() {
        addDisposable(loadFromDb()
                .subscribeOn(Schedulers.io())

                // Update UI with database content
                .observeOn(AndroidSchedulers.mainThread())
                .map { entity ->
                    Timber.e("Entity $entity")
                    val domain = entityToDomain(entity)
                    Timber.e("Domain $domain")
                    val shouldFetch = shouldFetch(entity)
                    Timber.e("shouldFetch $shouldFetch")
                    if (shouldFetch) {
                        updateLiveData(Resource.loading(domain))
                    } else {
                        updateLiveData(Resource.success(domain))
                    }
                    shouldFetch
                }

                // Check if we need to fetch data from network
                .observeOn(Schedulers.io())
                .map { shouldFetch -> if (shouldFetch) fetchFromNetwork() }
                .doOnComplete { fetchFromNetwork() } // No data from DB

                // Update UI if no network fetch required or impossible to acquire data
                .observeOn(AndroidSchedulers.mainThread())// Managed by LiveData
                //.doOnSuccess { updateLiveData(Resource.success(dtoToDomain(it))) }
                .doOnError { throwable -> updateLiveData(Resource.error(throwable.message, null, throwable)) }
                .subscribe())
    }

    /**
     * Ensure LiveData is updated in Main/UI thread, and avoid useless notification.
     */
    @MainThread
    private fun updateLiveData(newValue: Resource<Domain>) {
        if (!Objects.equals(result.value, newValue)) {
            result.value = newValue
        }
    }

    @WorkerThread
    private fun fetchFromNetwork() {
        Timber.e("fetchFromNetwork")
        addDisposable(createRemoteCall()
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    Timber.e("dto $it")
                    val domain = dtoToDomain(it)
                    Timber.e("Domain $domain")
                    val entity = domainToEntity(domain)
                    Timber.e("Entity $entity")
                    saveRemoteCallResult(entity) // Save remote data in DB
                    startDatabaseLoading() // Wait DB dispatch to ensure the single source of truth
                }
                .doOnError { onFetchFailed() } // Remote error, retry policy to be defined in the child class

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({},
                        { throwable -> updateLiveData(Resource.error(throwable.message, result.value?.data, throwable)) },// TODO: Not sure about this part
                        { error("Should not appear, please contact administration with this error.") })
        )
    }

    fun asLiveData(): LiveData<Resource<Domain>> {
        return result
    }

    @WorkerThread
    protected open fun onFetchFailed() {
    }

    @WorkerThread
    protected abstract fun saveRemoteCallResult(entity: Entity)

    @WorkerThread
    protected abstract fun shouldFetch(entity: Entity?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): Maybe<Entity>

    @WorkerThread
    protected abstract fun createRemoteCall(): Maybe<Dto>

    @WorkerThread
    protected abstract fun entityToDomain(entity: Entity?): Domain?

    @WorkerThread
    protected abstract fun domainToEntity(domain: Domain): Entity

    @WorkerThread
    protected abstract fun dtoToDomain(dto: Dto): Domain
}