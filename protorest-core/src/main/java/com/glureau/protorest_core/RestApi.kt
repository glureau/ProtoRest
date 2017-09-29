package com.glureau.protorest_core

import io.reactivex.Observable
import kotlin.reflect.KClass

open class RestApi(val baseApi: String) {
    fun rest(path: String, converters: Map<String, KClass<out Any>>): Observable<RestResult> {
        return Observable.empty<RestResult>()
    }
}