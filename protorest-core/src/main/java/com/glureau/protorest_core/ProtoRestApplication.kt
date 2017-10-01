package com.glureau.protorest_core

import android.app.Application
import android.graphics.Color
import android.view.View
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers


open class ProtoRestApplication<out A : RestApi>(val api: A) : Application() {
    lateinit var setup: List<RestFeature<*>>

    // TODO : Move this code in a dedicated class.
    inline fun <reified T> generateViews(feature: RestFeature<T>): Observable<List<View>> {
        return feature.observable()
                .observeOn(AndroidSchedulers.mainThread())
                .map({ next ->
                    val views = mutableListOf<View>()
                    val data: T = next.data
                    for (memberProperty in T::class.members.filter { it.javaClass.toString() == "class kotlin.reflect.jvm.internal.KProperty1Impl" }) {
                        val res = memberProperty.call(data).toString()
                        val newView = TextView(this)
                        newView.text = "${memberProperty.name} = $res"
                        newView.setTextColor(Color.RED)
                        views.add(newView)
                    }
                    views
                })
    }
}
