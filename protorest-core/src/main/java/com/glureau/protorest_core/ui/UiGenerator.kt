package com.glureau.protorest_core.ui

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.glureau.protorest_core.R
import com.glureau.protorest_core.RestFeature
import com.glureau.protorest_core.reflection.Reflection
import com.glureau.protorest_core.ui.generator.*
import com.glureau.protorest_core.ui.generator.UiGenerator
import com.glureau.protorest_core.ui.matcher.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.simple_object.view.*
import timber.log.Timber
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

object UiGenerator {

    val mapping = listOf(
            NumberMatcher to SimpleTextGenerator,
            BooleanMatcher to SimpleTextGenerator,
            DateMatcher to SimpleTextGenerator,
            ImageListMatcher to SimpleImageListGenerator,
            ImageMatcher to SimpleImageGenerator,
            StringMatcher to SimpleTextGenerator,
            StringListMatcher to SimpleTextListGenerator
    )

    private fun getSpecificGenerator(kClass: KClass<*>, kCallable: KCallable<*>): UiGenerator? {
        return mapping.firstOrNull { it.first.match(kCallable.returnType.jvmErasure, Reflection.annotations(kClass, kCallable)) }?.second
    }

    fun <T : Any> generateViews(activity: Activity, feature: RestFeature<T>, root: ViewGroup): Observable<List<View>> {
        return feature.observable()
                .observeOn(AndroidSchedulers.mainThread())
                .map { next ->
                    val dataClass = next.data.javaClass.kotlin
                    generateViewsRecursively(activity, next.data, /*generateAdditionalData(next.data, dataClass), */dataClass, root)
                }
    }
//
//    @PublishedApi internal fun <T> generateAdditionalData(data: T, dataClass: KClass<*>): MutableMap<Any, Any> {
//        val enhancements: MutableMap<Any, Any> = mutableMapOf()
//        for (memberProperty in Reflection.properties(dataClass)) {
//            val value = memberProperty.call(data) ?: continue
//            if (value is Collection<*> && Reflection.hasAnnotation(Reflection.annotations(dataClass, memberProperty), RestApi.Image::class)) {
//                value.filterIsInstance<String>()
//                        .forEach { elem ->
//                            enhancements.put(value, prepareImageLoading(elem))
//                        }
//            } else if (Reflection.hasAnnotation(Reflection.annotations(dataClass, memberProperty), RestApi.Image::class)) {
//                value as? String ?: error("@Image should be used on String only")
//                enhancements.put(value, prepareImageLoading(value))
//            } else if (!value.javaClass.isPrimitive && value !is Date) {
//                enhancements.putAll(generateAdditionalData(value, value::class))
//            }
//        }
//        return enhancements
//    }
//
//    private fun prepareImageLoading(url: String) = RestNetworkClient.get(url)
//            .observeOn(Schedulers.computation())
//            .map { BitmapFactory.decodeStream(it.body()?.byteStream()) }

    @PublishedApi internal fun <T> generateViewsRecursively(activity: Activity, data: T, /*additionalData: MutableMap<Any, Any>, */dataType: KClass<*>, root: ViewGroup): MutableList<View> {
        val views = mutableListOf<View>()
        Timber.i("Generate root views for class %s (%s)", dataType.simpleName, dataType.qualifiedName)
        for (memberProperty in Reflection.properties(dataType)) {
            val value = memberProperty.call(data)
            if (value == null) {
                Timber.w("Null value is ignored: %s", memberProperty.name)
                continue
            }
            val specificGenerator = getSpecificGenerator(dataType, memberProperty)
            if (specificGenerator == null) {
                val (containerView, container) = layout(activity, memberProperty.name, root)
                if (value is Collection<*>) {
                    for (elem in value) {
                        elem ?: continue
                        val elemUiGenerator = getSpecificGenerator(elem::class, memberProperty)
                        if (elemUiGenerator == null) {
                            TODO("Check this configuration (need mock server now :) )")
                            Timber.w("Member without ui generator in '%s' of class %s", memberProperty.name, memberProperty.returnType.jvmErasure)
                            generateViewsRecursively(activity, elem, /*additionalData, */memberProperty.returnType.jvmErasure, container).forEach { container.addView(it) }
                            views.add(containerView)
                        } else {
                            Timber.i("Member %s", memberProperty.name)
                            views.add(elemUiGenerator.generate(activity, memberProperty.name, value, root/*, additionalData*/))
                        }
                    }
                } else {
                    Timber.w("Member without ui generator '%s' of class %s", memberProperty.name, memberProperty.returnType.jvmErasure)
                    generateViewsRecursively(activity, value, /*additionalData, */memberProperty.returnType.jvmErasure, container).forEach { container.addView(it) }
                    views.add(containerView)
                }
            } else {
                Timber.i("Member %s", memberProperty.name)
                views.add(specificGenerator.generate(activity, memberProperty.name, value, root/*, additionalData*/))
            }
        }
        Timber.i("Generated views %d", views.count())
        return views
    }
}

@PublishedApi internal fun layout(activity: Activity, name: String, root: ViewGroup): Pair<View, ViewGroup> {
    val newView = activity.layoutInflater.inflate(R.layout.simple_object, root, false)
    newView.simpleObjectLabel.text = name
    return newView to newView.simpleObjectContainer
}


