package com.glureau.geno.lib.rest

import com.glureau.geno.lib.network.RestNetworkClient
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Rfc3339DateJsonAdapter
import com.squareup.moshi.Types
import timber.log.Timber
import java.util.*

open class RestApi(val baseApi: String, adapters: Array<Any> = emptyArray()) {

    private val moshi: Moshi
//    private lateinit var errorClass: Class<*>

    init {
        var builder = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
                .add(Types.newParameterizedType(List::class.java, String::class.java).rawType, StringArrayJsonAdapter)
        adapters.forEach { builder = builder.add(it) }
        moshi = builder.build()

//        val restErrorAnnotation = this::class.java.declaredAnnotations.filter { it is RestError }.firstOrNull()
//        if (restErrorAnnotation != null) {
//            // TODO : do better than string for names...
//            errorClass = RestError::class.java.getMethod("errorKClass").invoke(restErrorAnnotation) as Class<*>
//        }
    }

    fun get(path: String, clazz: Class<*>): RestResult<Any> {
        val response = RestNetworkClient.get(baseApi + path)
        Timber.d("Headers: ${response.headers()}")
        val body = response.body()?.string()
        if (body == null) {
            Timber.e("Error when requesting ${response.request().url()} : body is null (status ${response.code()})")
            return RestResult(Any())
        }
        return parseResult(body, if (response.isSuccessful) clazz else TODO("Parse custom error class"))
    }

    private fun parseResult(body: String, clazz: Class<*>): RestResult<Any> {
        val jsonAdapter = moshi.adapter(clazz).lenient()
        val result = jsonAdapter.fromJson(body)
        Timber.i("Response parsed: %s", result)
        if (result == null) {
            error("Unable to parse")
        } else {
            return RestResult(result)
        }
    }
}
