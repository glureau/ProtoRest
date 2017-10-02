package com.glureau.protorest_core.ui

import android.app.Activity
import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import com.glureau.protorest_core.R
import com.glureau.protorest_core.RestFeature
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.simple_text.view.*
import kotlinx.android.synthetic.main.simple_text2.view.*
import timber.log.Timber
import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

class UiGenerator {
    companion object {
        val mapping = mapOf<KClass<*>, (Activity, String, Any, ViewGroup) -> View>(
                // Numbers
                Double::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Float::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Long::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Int::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Short::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Byte::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },

                String::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Boolean::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Date::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) }
        )

        val mapping2 = mapOf<KClass<*>, (Activity, String, Any, ViewGroup) -> View>(
                // Numbers
                Double::class to { activity, name, res, root -> createSimpleText2(name, res, activity, root) },
                Float::class to { activity, name, res, root -> createSimpleText2(name, res, activity, root) },
                Long::class to { activity, name, res, root -> createSimpleText2(name, res, activity, root) },
                Int::class to { activity, name, res, root -> createSimpleText2(name, res, activity, root) },
                Short::class to { activity, name, res, root -> createSimpleText2(name, res, activity, root) },
                Byte::class to { activity, name, res, root -> createSimpleText2(name, res, activity, root) },

                String::class to { activity, name, res, root -> createSimpleText2(name, res, activity, root) },
                Boolean::class to { activity, name, res, root -> createSimpleText2(name, res, activity, root) },
                Date::class to { activity, name, res, root -> createSimpleText2(name, res, activity, root) }
        )

        inline fun <reified T> generateViews(activity: Activity, feature: RestFeature<T>, root: ViewGroup): Observable<List<View>> {
            return feature.observable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { next ->
                        next::class // Try to remove reified to use java reflection instead? (use the class as parameter in the method belows)
                        generateViewsRoot(activity, next.data, root)
                    }
        }

        @PublishedApi internal inline fun <reified T> generateViewsRoot(activity: Activity, data: T, root: ViewGroup): MutableList<View> {
            val views = mutableListOf<View>()
            Timber.i("Generate root views for class %s (%s)", T::class.simpleName, T::class.qualifiedName)
            for (memberProperty in T::class.members.filter { it is KProperty }) {
                val value = getValue(memberProperty, data) ?: continue // Ignore null value
                val mapper = mapping[memberProperty.returnType.jvmErasure]
                if (mapper == null) {
                    Timber.w("Member without mapper '%s' of class %s", memberProperty.name, memberProperty.returnType.jvmErasure)
                    views.addAll(generateViewsLeaf(activity, value, memberProperty.returnType.jvmErasure, root))
                } else {
                    Timber.i("Member %s", memberProperty.name)
                    views.add(mapper.invoke(activity, memberProperty.name, value, root))
                }
            }
            Timber.i("Generated views %d", views.count())
            return views
        }

        @PublishedApi internal fun generateViewsLeaf(activity: Activity, data: Any, dataType: KClass<*>, root: ViewGroup): MutableList<View> {
            val views = mutableListOf<View>()
            Timber.e("Generate leaf views for class %s (%s)", dataType.simpleName, dataType.qualifiedName)
            for (memberProperty in dataType.members.filter { it is KProperty }) {
                val value = getValue(memberProperty, data) ?: continue // Ignore null value
                val mapper2 = mapping2[memberProperty.returnType.jvmErasure]
                Timber.e("Member %s", memberProperty.name)
                if (mapper2 == null) {
                    views.add(createSimpleText2(memberProperty.name, value, activity, root))
                } else {
                    views.add(mapper2.invoke(activity, memberProperty.name, value, root))
                }
            }
            Timber.e("Generated views %d", views.count())
            return views
        }

        @PublishedApi internal fun <T> getValue(memberProperty: KCallable<*>, target: T): Any? {
            val value = memberProperty.call(target)
            if (value == null) {
                Timber.w("Null value is ignored: %s", memberProperty.name)
            }
            return value
        }
    }
}

@PublishedApi internal fun <T> createSimpleText(name: String, data: T, activity: Activity, root: ViewGroup): View {
    val result = data.toString()
    val newView = activity.layoutInflater.inflate(R.layout.simple_text, root, false)
    newView.simpleTextLabel.text = name
    newView.simpleTextValue.text = result
    return newView
}

@PublishedApi internal fun <T> createSimpleText2(name: String, data: T, activity: Activity, root: ViewGroup): View {
    val result = data.toString()
    val newView = activity.layoutInflater.inflate(R.layout.simple_text2, root, false)
    newView.simpleTextLabel2.text = name
    newView.simpleTextValue2.text = result
    return newView
}
