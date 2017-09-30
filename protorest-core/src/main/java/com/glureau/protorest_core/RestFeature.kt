package com.glureau.protorest_core

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class RestFeature(val name: String,
                  val action: () -> Observable<RestResult>) {
    fun observable(): Observable<RestResult> = action.invoke().subscribeOn(Schedulers.io())

}