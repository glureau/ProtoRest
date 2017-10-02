package com.glureau.protorest_core.ui

import android.app.Activity
import android.graphics.Bitmap
import android.media.Image
import android.view.View
import android.view.ViewGroup
import com.glureau.protorest_core.R
import com.glureau.protorest_core.RestApi
import com.glureau.protorest_core.RestFeature
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.simple_image.view.*
import kotlinx.android.synthetic.main.simple_object.view.*
import kotlinx.android.synthetic.main.simple_text.view.*
import okhttp3.OkHttpClient
import timber.log.Timber
import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

class UiGenerator {
    companion object {
        val client = OkHttpClient()
        val mapping = mapOf<KClass<*>, (Activity, String, Any, ViewGroup) -> View>(
                // Numbers
                Double::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Float::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Long::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Int::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Short::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Byte::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },

                String::class to { activity, name, res, root -> createSimpleText(name, res as String, activity, root) },
                Boolean::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },
                Date::class to { activity, name, res, root -> createSimpleText(name, res, activity, root) },

                // Custom
                RestApi.Image::class to { activity, name, res, root -> createSimpleImage(name, (res as RestApi.Image).bitmap, activity, root) }
        )


        fun <T : Any> generateViews(activity: Activity, feature: RestFeature<T>, root: ViewGroup): Observable<List<View>> {
            return feature.observable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { next ->
                        val data = next.data
                        val dataClass = data.javaClass.kotlin
                        generateViewsRecursively(activity, next.data, dataClass, root)
                    }
        }

        @PublishedApi internal fun <T> generateViewsRecursively(activity: Activity, data: T, dataType: KClass<*>, root: ViewGroup): MutableList<View> {
            val views = mutableListOf<View>()
            Timber.i("Generate root views for class %s (%s)", dataType.simpleName, dataType.qualifiedName)
            for (memberProperty in dataType.members.filter { it is KProperty }) {
                val value = getValue(memberProperty, data) ?: continue // Ignore null value
                val mapper = mapping[memberProperty.returnType.jvmErasure]
                if (mapper == null) {
                    Timber.w("Member without mapper '%s' of class %s", memberProperty.name, memberProperty.returnType.jvmErasure)
                    val (containerView, container) = layout(activity, memberProperty.name, root)
                    generateViewsRecursively(activity, value, memberProperty.returnType.jvmErasure, container).forEach { container.addView(it) }
                    views.add(containerView)
                } else {
                    Timber.i("Member %s", memberProperty.name)
                    views.add(mapper.invoke(activity, memberProperty.name, value, root))
                }
            }
            Timber.i("Generated views %d", views.count())
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

@PublishedApi internal fun layout(activity: Activity, name: String, root: ViewGroup): Pair<View, ViewGroup> {
    val newView = activity.layoutInflater.inflate(R.layout.simple_object, root, false)
    newView.simpleObjectLabel.text = name
    return newView to newView.simpleObjectContainer
}

@PublishedApi internal fun <T> createSimpleText(name: String, data: T, activity: Activity, root: ViewGroup): View {
    val result = data.toString()
    val newView = activity.layoutInflater.inflate(R.layout.simple_text, root, false)
    newView.simpleTextLabel.text = name
    newView.simpleTextValue.text = result
    return newView
}

@PublishedApi internal fun createSimpleImage(name: String, bitmap: Bitmap, activity: Activity, root: ViewGroup): View {
    val newView = activity.layoutInflater.inflate(R.layout.simple_image, root, false)
    newView.simpleImageLabel.text = name
    newView.simpleImageValue.setImageBitmap(bitmap)
    return newView
}

