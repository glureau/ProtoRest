package com.glureau.protorest_core

import android.util.Log
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request

open class RestApi(val baseApi: String) {
    val moshi = Moshi.Builder().build()
    val client = OkHttpClient()
    fun <T> rest(path: String, clazz: Class<T>): Observable<RestResult<T>> =
            Observable.create<RestResult<T>> { s ->
                val req = Request.Builder().url(baseApi + path).build()
                try {
                    val response = client.newCall(req).execute()
                    val body = response.body()?.string()
                    val jsonAdapter = moshi.adapter(clazz).lenient()
                    Log.e("JSON", body)
                    val result = jsonAdapter.fromJson(body) as T
                    Log.e("RESULT", result.toString())
                    s.onNext(RestResult(result))
                    s.onComplete()
                } catch (t: Throwable) {
                    s.onError(t)
                }
            }
}
