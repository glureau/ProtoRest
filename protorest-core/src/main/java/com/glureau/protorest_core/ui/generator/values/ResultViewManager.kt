package com.glureau.protorest_core.ui.generator.values

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.glureau.protorest_core.R
import com.glureau.protorest_core.reflection.Reflection
import com.glureau.protorest_core.ui.matcher.*
import kotlinx.android.synthetic.main.field_object.view.*
import timber.log.Timber
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

internal class ResultViewManager(val activity: Activity, val resultContainer: ViewGroup) {

    val mapping = listOf(
            NumberMatcher to SimpleTextGenerator,
            BooleanMatcher to SimpleTextGenerator,
            DateMatcher to SimpleTextGenerator,
            ImageListMatcher to SimpleImageListGenerator,
            ImageMatcher to SimpleImageGenerator,
            StringMatcher to SimpleTextGenerator,
            StringListMatcher to SimpleTextListGenerator,
            NumberListMatcher to SimpleTextListGenerator
    )

    private fun getSpecificGenerator(kClass: KClass<*>, kCallable: KCallable<*>): ValueViewGenerator<Any>? {
        // TODO: fix this 'Unchecked cast' with a more elegant generics management.
        return mapping.firstOrNull { it.first.match(kCallable.returnType.jvmErasure, Reflection.fieldAnnotations(kClass, kCallable)) }?.second as ValueViewGenerator<Any>?
    }

    fun <T : Any> updateResultView(data: T, dataType: KClass<*>) {
        resultContainer.removeAllViews()
        generateViewsRecursively(data, dataType, resultContainer).forEach {
            Timber.wtf("isAttached ? $it -> ${it.parent}")
            if (it.parent == null)
                resultContainer.addView(it)
        }
    }

    private fun <T : Any> generateViewsRecursively(data: T, dataType: KClass<*>, root: ViewGroup): MutableList<View> {
        val views = mutableListOf<View>()
        Timber.i("Generate root views for class %s (%s)", dataType.simpleName, dataType.qualifiedName)
        if (data is Array<*>) {
            val (containerView, container) = layout(null, root)
            data.filter { it != null }.forEach { elem ->
                Timber.e("Data type $dataType")
                generateViewsRecursively(elem!!, dataType.java.componentType.kotlin, container).forEach { container.addView(it) }
                views.add(containerView)
            }
        }
        for (memberProperty in Reflection.properties(dataType)) {
            val value = memberProperty.call(data)
            if (value == null) {
                Timber.w("Null value is ignored: %s", memberProperty.name)
                continue
            }
            val specificGenerator = getSpecificGenerator(dataType, memberProperty)
            if (specificGenerator == null) {
                val (containerView, container) = layout(memberProperty.name, root)
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
                    generateViewsRecursively(value, memberProperty.returnType.jvmErasure, container).forEach { container.addView(it) }
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

    private fun layout(name: String?, root: ViewGroup): Pair<View, ViewGroup> {
        val newView = activity.layoutInflater.inflate(R.layout.field_object, root, false)
        newView.fieldObjectLabel.visibility = if (name == null) View.GONE else View.VISIBLE
        newView.fieldObjectLabel.text = name
        return newView to newView.fieldObjectContainer
    }
}
