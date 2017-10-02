package com.glureau.protorest_core

import android.graphics.BitmapFactory
import com.glureau.protorest_core.network.RestNetworkClient
import com.squareup.moshi.*
import io.reactivex.Observable
import timber.log.Timber
import java.util.*

open class RestApi(val baseApi: String) {

    annotation class Image

    val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build()

    fun <T> get(path: String, clazz: Class<T>): Observable<RestResult<T>> =
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
                            enhance(result)
                            RestResult(result)
                        }
                    }

    private fun <T> enhance(result: T) {
        result::class.members
        RestNetworkClient.get(path)
                .subscribe {
                    bitmap = BitmapFactory.decodeStream(it.body()?.byteStream())
                }
    }
}
