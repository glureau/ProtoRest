package com.glureau.protorest_core

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.reflect.KClass


open class RestApi(val baseApi: String) {
    val client = OkHttpClient()
    fun rest(path: String, converters: Map<String, KClass<out Any>>): Observable<RestResult> =
            Observable.create<RestResult> { s ->
                val req = Request.Builder().url(baseApi + path).build()
                try {
                    val response = client.newCall(req).execute()
                    val body = response.body().toString()
                    val obj = JSONObject(body)
                    s.onNext(RestResult(obj))
                    s.onComplete()
                } catch (t: Throwable) {
                    s.onError(t)
                }
            }
}