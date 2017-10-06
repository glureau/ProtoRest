package com.glureau.protorest_core.rest

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class RestFeature<T>(val name: String,
                     val action: () -> Observable<RestResult<T>>,
                     val generateViews: (Activity, RestFeature<T>, ViewGroup) -> Observable<List<View>>) {
    fun generateViews(activity: Activity, root: ViewGroup) = generateViews.invoke(activity, this, root)

    fun observable(): Observable<RestResult<T>> = action.invoke().subscribeOn(Schedulers.io())
}

class RestFeatureGroup(val name: String, val features: List<RestFeature<*>>)