package com.glureau.protorest_core

import android.app.Application
import android.view.View
import com.glureau.protorest_core.ui.UiGenerator
import io.reactivex.Observable


open class ProtoRestApplication<out A : RestApi>(val api: A) : Application() {
    lateinit var setup: List<RestFeature<*>>

    inline fun <reified T> generateViews(feature: RestFeature<T>): Observable<List<View>> = UiGenerator.generateViews(this, feature)
}
