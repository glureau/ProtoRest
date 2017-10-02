package com.glureau.protorest_core.network

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class RestNetworkClient {
    companion object {
        val client = OkHttpClient()
        fun get(url: String): Observable<Response> = response(Request.Builder().url(url).build())

        @PublishedApi internal fun response(req: Request): Observable<Response> =
                Observable.create<Response> { s ->
                    try {
                        s.onNext(client.newCall(req).execute())
                        s.onComplete()
                    } catch (t: Throwable) {
                        s.onError(t)
                    }
                }.subscribeOn(Schedulers.io())

        fun contentType(url: String): Observable<String> =
                response(Request.Builder().url(url).head().build())
                        .map { it.header("Content-Type").toString() }

    }
}