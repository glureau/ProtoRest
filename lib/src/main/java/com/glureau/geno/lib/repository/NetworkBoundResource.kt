package com.glureau.geno.lib.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.glureau.geno.lib.rx.DisposableManager
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


/**
 * A RX generic class that can provide a resource backed by both the sqlite database and the network.
 * You can read more about it in the [Architecture Guide](https://developer.android.com/arch).
 * @param <ResultType>
 */
abstract class NetworkBoundResource<ResultType> @MainThread protected constructor() : DisposableManager() {

    private val result = MutableLiveData<Resource<ResultType>>()

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
                .doOnSuccess { data -> updateLiveData(Resource.loading(data)) }

                // Check if we need to fetch data from network
                .observeOn(Schedulers.io())
                .map { data -> shouldFetch(data) to data }
                .doOnComplete { fetchFromNetwork() } // No data from DB
                .doOnSuccess { (shouldFetch, _) -> if (shouldFetch) fetchFromNetwork() }

                // Update UI if no network fetch required or impossible to acquire data
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { (shouldFetch, data) -> if (!shouldFetch) updateLiveData(Resource.success(data)) }
                .doOnError { throwable -> updateLiveData(Resource.error(throwable.message, null, throwable)) }
                .subscribe())
    }

    /**
     * Ensure LiveData is updated in Main/UI thread, and avoid useless notification.
     */
    @MainThread
    private fun updateLiveData(newValue: Resource<ResultType>) {
        if (!Objects.equals(result.value, newValue)) {
            result.value = newValue
        }
    }

    @WorkerThread
    private fun fetchFromNetwork() {
        addDisposable(createRemoteCall()
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    saveRemoteCallResult(it) // Save remote data in DB
                    startDatabaseLoading() // Wait DB dispatch to ensure the single source of truth
                }
                .doOnError { onFetchFailed() } // Remote error, retry policy to be defined in the child class

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({},
                        { throwable -> updateLiveData(Resource.error(throwable.message, result.value?.data, throwable)) },// TODO: Not sure about this part
                        { error("Should not appear, please contact administration with this error.") })
        )
    }

    fun asLiveData(): LiveData<Resource<ResultType>> {
        return result
    }

    @WorkerThread
    protected open fun onFetchFailed() {
    }

    @WorkerThread
    protected abstract fun saveRemoteCallResult(item: ResultType)

    @WorkerThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): Maybe<ResultType>

    @WorkerThread
    protected abstract fun createRemoteCall(): Maybe<ResultType>

}