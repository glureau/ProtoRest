package com.glureau.protorest_core.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import com.glureau.protorest_core.R
import com.glureau.protorest_core.RestApi
import com.glureau.protorest_core.RestFeature
import com.glureau.protorest_core.network.RestNetworkClient
import com.glureau.protorest_core.reflection.Reflection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.simple_image.view.*
import kotlinx.android.synthetic.main.simple_object.view.*
import kotlinx.android.synthetic.main.simple_text.view.*
import timber.log.Timber
import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class UiGenerator {
    companion object {

        val mapping = listOf<Pair<(KClass<*>, Array<out Annotation>?) -> Boolean, (Activity, String, Any, ViewGroup, Map<Any, Any>) -> View>>(
                { kClass: KClass<*>, _: Array<out Annotation>? -> kClass.isSubclassOf(Number::class) } to { activity, name, res, root, _ -> createSimpleText(name, res, activity, root) },
                { kClass: KClass<*>, _: Array<out Annotation>? -> kClass == Boolean::class } to { activity, name, res, root, _ -> createSimpleText(name, res, activity, root) },
                { kClass: KClass<*>, _: Array<out Annotation>? -> kClass == Date::class } to { activity, name, res, root, _ -> createSimpleText(name, res, activity, root) },
                { _: KClass<*>, annotations: Array<out Annotation>? -> hasAnnotation(annotations, RestApi.Image::class) } to { activity, name, res, root, enhancements -> createSimpleImage(name, res, enhancements, activity, root) },
                { kClass: KClass<*>, _: Array<out Annotation>? -> kClass == String::class } to { activity, name, res, root, _ -> createSimpleText(name, res, activity, root) }
        )

        private fun annotations(kClass: KClass<*>, kCallable: KCallable<*>) = kClass.java.declaredFields.firstOrNull { it.name == kCallable.name }?.annotations
        private fun hasAnnotation(annotations: Array<out Annotation>?, kClass: KClass<out Annotation>) = annotations?.firstOrNull { it.annotationClass == kClass } != null

        private fun mapper(kClass: KClass<*>, kCallable: KCallable<*>): ((Activity, String, Any, ViewGroup, Map<Any, Any>) -> View)? {
            return mapping.firstOrNull { it.first.invoke(kCallable.returnType.jvmErasure, annotations(kClass, kCallable)) }?.second
        }

        fun <T : Any> generateViews(activity: Activity, feature: RestFeature<T>, root: ViewGroup): Observable<List<View>> {
            return feature.observable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { next ->
                        val dataClass = next.data.javaClass.kotlin
                        generateViewsRecursively(activity, next.data, enhance(next.data, dataClass), dataClass, root)
                    }
        }

        @PublishedApi internal fun <T> enhance(data: T, dataClass: KClass<*>): MutableMap<Any, Any> {
            val enhancements: MutableMap<Any, Any> = mutableMapOf()
            for (memberProperty in Reflection.properties(dataClass)) {
                val value = memberProperty.call(data) ?: continue // No value => No enhancements
                if (hasAnnotation(annotations(dataClass, memberProperty), RestApi.Image::class)) {
                    value as? String ?: error("@Image should be used on String only")
                    enhancements.put(value, RestNetworkClient.get(value)
                            .observeOn(Schedulers.computation())
                            .map { BitmapFactory.decodeStream(it.body()?.byteStream()) })
                }

                if (!value.javaClass.isPrimitive) {
                    enhancements.putAll(enhance(value, value::class))
                }
            }
            return enhancements
        }

        @PublishedApi internal fun <T> generateViewsRecursively(activity: Activity, data: T, enhancements: MutableMap<Any, Any>, dataType: KClass<*>, root: ViewGroup): MutableList<View> {
            val views = mutableListOf<View>()
            Timber.i("Generate root views for class %s (%s)", dataType.simpleName, dataType.qualifiedName)
            for (memberProperty in Reflection.properties(dataType)) {
                val value = getValue(memberProperty, data) ?: continue // Ignore null value
                val mapper = mapper(dataType, memberProperty)
                if (mapper == null) {
                    Timber.w("Member without mapper '%s' of class %s", memberProperty.name, memberProperty.returnType.jvmErasure)
                    val (containerView, container) = layout(activity, memberProperty.name, root)
                    generateViewsRecursively(activity, value, enhancements, memberProperty.returnType.jvmErasure, container).forEach { container.addView(it) }
                    views.add(containerView)
                } else {
                    Timber.i("Member %s", memberProperty.name)
                    views.add(mapper.invoke(activity, memberProperty.name, value, root, enhancements))
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

@PublishedApi internal fun createSimpleImage(name: String, data: Any, enhancements: Map<Any, Any>, activity: Activity, root: ViewGroup): View {
    val newView = activity.layoutInflater.inflate(R.layout.simple_image, root, false)
    newView.simpleImageLabel.text = name
    val obs = enhancements[data]
    if (obs != null) {
        (obs as Observable<Bitmap>)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // TODO : Check the view, it could have disappear when we receive the Bitmap
                    newView.simpleImageValue.setImageBitmap(it)
                }
    }
    return newView
}

