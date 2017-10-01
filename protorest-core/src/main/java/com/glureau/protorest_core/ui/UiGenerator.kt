package com.glureau.protorest_core.ui

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.glureau.protorest_core.RestFeature
import com.glureau.protorest_core.RestResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

class UiGenerator {
    companion object {
        val mapping = mapOf<KClass<*>, (Context, KCallable<*>, RestResult<*>) -> View>(
                Long::class to { c, mP, res ->
                    val result = mP.call(res.data).toString()
                    val newView = TextView(c)
                    newView.text = "${mP.name} = $result"
                    newView.setTextColor(Color.RED)
                    newView
                },
                String::class to { c, mP, res ->
                    val result = mP.call(res.data).toString()
                    val newView = TextView(c)
                    newView.text = "${mP.name} = $result"
                    newView.setTextColor(Color.RED)
                    newView
                }
        )

        inline fun <reified T> generateViews(context: Context, feature: RestFeature<T>): Observable<List<View>> {
            return feature.observable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map({ next ->
                        val views = mutableListOf<View>()
                        Timber.i("Generate views for class ", T::class.qualifiedName)
                        for (memberProperty in T::class.members.filter { it is KProperty }) {
                            val mapper = mapping[memberProperty.returnType.jvmErasure] ?: continue
                            views.add(mapper.invoke(context, memberProperty, next))
                        }
                        views
                    })
        }
    }
}