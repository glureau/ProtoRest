package com.glureau.protorest_core.rest

import android.app.Activity
import android.view.ViewGroup
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class RestFeature<T>(val name: String,
                     val action: (params: Array<out RestParameter>) -> Observable<RestResult<T>>,
                     val generateViews: (Activity, RestFeature<T>, ViewGroup, ViewGroup) -> Completable,
                     val params: Array<out RestParameter>) {
    fun generateViews(activity: Activity, paramContainer: ViewGroup, resultContainer: ViewGroup) = generateViews.invoke(activity, this, paramContainer, resultContainer)

    fun observable(): Observable<RestResult<T>> = action.invoke(params).subscribeOn(Schedulers.io())
}

class RestFeatureGroup(val name: String, val features: List<RestFeature<*>>)