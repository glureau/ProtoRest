package com.glureau.protorest_core

import android.view.View
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class RestFeature<T>(val name: String,
                     val action: () -> Observable<RestResult<T>>,
                     val generateViews: (RestFeature<T>) -> Observable<List<View>>) {
    fun generateViews() = generateViews.invoke(this)

    fun observable(): Observable<RestResult<T>> = action.invoke().subscribeOn(Schedulers.io())
}