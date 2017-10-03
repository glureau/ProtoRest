package com.glureau.protorest_core

import com.glureau.protorest_core.network.RestNetworkClient
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Rfc3339DateJsonAdapter
import io.reactivex.Observable
import timber.log.Timber
import java.util.*

open class RestApi(val baseApi: String) {

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FIELD)
    annotation class Image

    val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build()

    inline fun <reified T> get(path: String, clazz: Class<T>): Observable<RestResult<T>> =
            RestNetworkClient.get(baseApi + path)
                    .map { response ->
                        val body = response.body()?.string()
                        if (body != null)
                            Timber.i("Receive response from server: %s", body)
                        val jsonAdapter = moshi.adapter(clazz).lenient()
                        val result = jsonAdapter.fromJson(body)
                        Timber.i("Response parsed: %s", result)
                        if (result == null) {
                            error("Unable to parse")
                        } else {
                            RestResult(result)
                        }
                    }


}
