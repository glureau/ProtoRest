package com.glureau.geno.old.network

import okhttp3.OkHttpClient
import okhttp3.Request

class RestNetworkClient {
    companion object {
        val client = OkHttpClient()
        fun get(url: String) = response(Request.Builder().url(url).build())

        @PublishedApi internal fun response(req: Request) = client.newCall(req).execute()

        fun contentType(url: String) = response(Request.Builder().url(url).head().build()).header("Content-Type")
    }
}