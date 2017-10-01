package com.glureau.protorest_core.ui

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.glureau.protorest_core.R
import com.glureau.protorest_core.RestFeature
import com.glureau.protorest_core.RestResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.simple_text.view.*
import timber.log.Timber
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

class UiGenerator {
    companion object {
        val mapping = mapOf<KClass<*>, (Activity, KCallable<*>, RestResult<*>, ViewGroup) -> View>(
                Long::class to { activity, mP, res, root -> createSimpleText(mP, res, activity, root) } ,
                String::class to { activity, mP, res, root -> createSimpleText(mP, res, activity, root) }
        )

        inline fun <reified T> generateViews(activity: Activity, feature: RestFeature<T>, root: ViewGroup): Observable<List<View>> {
            return feature.observable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map({ next ->
                        val views = mutableListOf<View>()
                        Timber.i("Generate views for class %s", T::class.qualifiedName)
                        for (memberProperty in T::class.members.filter { it is KProperty }) {
                            val mapper = mapping[memberProperty.returnType.jvmErasure] ?: continue
                            views.add(mapper.invoke(activity, memberProperty, next, root))
                        }
                        Timber.i("Generated views %d", views.count())
                        views
                    })
        }
    }
}

private fun createSimpleText(mP: KCallable<*>, res: RestResult<*>, activity: Activity, root: ViewGroup): View {
    val result = mP.call(res.data).toString()
    val newView = activity.layoutInflater.inflate(R.layout.simple_text, root, false)
    newView.simpleTextLabel.text = mP.name
    newView.simpleTextValue.text = result
    return newView
}
