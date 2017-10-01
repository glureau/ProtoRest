package com.glureau.protorest_core

import com.squareup.moshi.Moshi
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber

open class RestApi(val baseApi: String) {
    val moshi: Moshi = Moshi.Builder().build()
    val client = OkHttpClient()
    fun <T> rest(path: String, clazz: Class<T>): Observable<RestResult<T>> =
            Observable.create<RestResult<T>> { s ->
                val req = Request.Builder().url(baseApi + path).build()
                try {
                    val response = client.newCall(req).execute()
                    val body = response.body()?.string()
                    if (body != null)
                    Timber.i("Receive response from server: %s", body)
                    val jsonAdapter = moshi.adapter(clazz).lenient()
                    val result = jsonAdapter.fromJson(body)
                    Timber.i("Response parsed: %s", result)
                    if (result != null) {
                        s.onNext(RestResult(result))
                    }
                    s.onComplete()
                } catch (t: Throwable) {
                    s.onError(t)
                }
            }
}
