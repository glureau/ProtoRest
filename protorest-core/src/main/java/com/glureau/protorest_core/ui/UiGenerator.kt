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
            StringListMatcher to SimpleTextListGenerator,
            NumberListMatcher to SimpleTextListGenerator,
            AnythingMatcher to SimpleTextGenerator
    )

    private fun getSpecificGenerator(kClass: KClass<*>, kCallable: KCallable<*>): UiGenerator<Any>? {
        // TODO: fix this 'Unchecked cast' with a more elegant generics management.
        return mapping.firstOrNull { it.first.match(kCallable.returnType.jvmErasure, Reflection.annotations(kClass, kCallable)) }?.second as UiGenerator<Any>?
    }

    fun <T : Any> generateViews(activity: Activity, feature: RestFeature<T>, root: ViewGroup): Observable<List<View>> {
        return feature.observable()
                .observeOn(AndroidSchedulers.mainThread())
                .map { next ->
                    val dataClass = next.data.javaClass.kotlin
                    generateViewsRecursively(activity, next.data, dataClass, root)
                }
    }

    @PublishedApi internal fun <T> generateViewsRecursively(activity: Activity, data: T, dataType: KClass<*>, root: ViewGroup): MutableList<View> {
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
                        } else {
                            Timber.i("Member %s", memberProperty.name)
                            views.add(elemUiGenerator.generate(activity, memberProperty.name, value, root))
                        }
                    }
                } else {
                    Timber.w("Member without ui generator '%s' of class %s", memberProperty.name, memberProperty.returnType.jvmErasure)
                    generateViewsRecursively(activity, value, memberProperty.returnType.jvmErasure, container).forEach { container.addView(it) }
                    views.add(containerView)
                }
            } else {
                Timber.i("Member %s", memberProperty.name)
                views.add(specificGenerator.generate(activity, memberProperty.name, value, root))
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


