package com.glureau.geno.old.ui.generator.values

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.glureau.geno.R
import com.glureau.geno.old.reflection.Reflection
import com.glureau.geno.old.ui.matcher.TypeMatcher
import kotlinx.android.synthetic.main.field_object.view.*
import timber.log.Timber
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

internal class ResultViewManager(val activity: Activity, val resultContainer: ViewGroup, val typeToViewGenerator: List<Pair<TypeMatcher, ValueViewGenerator<*>>>) {

    private fun getSpecificGenerator(kClass: KClass<*>, kCallable: KCallable<*>?): ValueViewGenerator<Any>? {
        // TODO: fix this 'Unchecked cast' with a more elegant generics management.
        if (kCallable != null)
            return typeToViewGenerator.firstOrNull { it.first.match(kCallable.returnType.jvmErasure, Reflection.fieldAnnotations(kClass, kCallable)) }?.second as ValueViewGenerator<Any>?
        else
            return typeToViewGenerator.firstOrNull { it.first.match(kClass, emptyArray()) }?.second as ValueViewGenerator<Any>?
    }


    fun <T : Any> updateResultView(data: T, dataType: KClass<*>) {
        resultContainer.removeAllViews()
        val generatedViews = generateViewsRecursively("", data, dataType, null, resultContainer)
        if (generatedViews.isEmpty()) {
            activity.layoutInflater.inflate(R.layout.no_data, resultContainer)
        } else {
            generatedViews.forEach {
                // TODO : Why a view is already attached to the parent ??!
                if (it.parent == null)
                    resultContainer.addView(it)
            }
        }
    }

    private fun <T : Any> generateViewsRecursively(name:String, data: T, dataType: KClass<*>, memberProperty: KCallable<*>?, root: ViewGroup): MutableList<View> {
        val views = mutableListOf<View>()
        Timber.i("Generate root views for class %s (%s)", dataType.simpleName, dataType.qualifiedName)

        val immediateViewGenerator = getSpecificGenerator(dataType, memberProperty)
        if (immediateViewGenerator != null) {
            // TODO : "" is not very explicit...
            views.add(immediateViewGenerator.generate(activity, name, data, root))
        } else if (data is Array<*>) {
            val (containerView, container) = layout(null, root)
            data.filter { it != null }.forEach { elem ->
                generateViewsRecursively(name, elem!!, dataType.java.componentType.kotlin, null, container).forEach { container.addView(it) }
                views.add(containerView)
            }
        } else {
            // No custom generator => auto-generating UI

            // TODO : Separate view creation and view filling
            val (containerView, container) = layout(null, root)
            for (property in Reflection.properties(dataType)) {
                val value = property.call(data)
                if (value == null) {
                    Timber.w("Null value is ignored: %s", property.name)
                    continue
                }
                generateViewsRecursively(property.name, value, dataType, property, container).forEach { container.addView(it) }
            }
            views.add(containerView)
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
