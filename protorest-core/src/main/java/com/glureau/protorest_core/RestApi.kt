package com.glureau.protorest_core

import io.reactivex.Observable
import kotlin.reflect.KClass

open class RestApi(val baseApi: String) {
    fun <R : RestResult> rest(path : String, klass : KClass<R>): Observable<R> {
        return Observable.empty<R>()
    }
}