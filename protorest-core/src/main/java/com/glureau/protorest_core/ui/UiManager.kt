package com.glureau.protorest_core.ui

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.glureau.protorest_core.R
import com.glureau.protorest_core.reflection.Reflection
import com.glureau.protorest_core.rest.RestFeature
import com.glureau.protorest_core.rest.RestResult
import com.glureau.protorest_core.ui.generator.*
import com.glureau.protorest_core.ui.matcher.*
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.field_object.view.*
import kotlinx.android.synthetic.main.parameter_text.view.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

class UiManager(val activity: Activity, val updateUiSubject: PublishSubject<Boolean>, val parameterContainer: ViewGroup, val resultContainer: ViewGroup) {

    private var mLastFeatureForParameters: RestFeature<out Any>? = null

    val mapping = listOf(
            NumberMatcher to FieldTextGenerator,
            BooleanMatcher to FieldTextGenerator,
            DateMatcher to FieldTextGenerator,
            ImageListMatcher to SimpleImageListGenerator,
            ImageMatcher to FieldImageGenerator,
            StringMatcher to FieldTextGenerator,
            StringListMatcher to SimpleTextListGenerator,
            NumberListMatcher to SimpleTextListGenerator
    )

    private fun getSpecificGenerator(kClass: KClass<*>, kCallable: KCallable<*>): UiGenerator<Any>? {
        // TODO: fix this 'Unchecked cast' with a more elegant generics management.
        return mapping.firstOrNull { it.first.match(kCallable.returnType.jvmErasure, Reflection.fieldAnnotations(kClass, kCallable)) }?.second as UiGenerator<Any>?
    }

    fun <T : Any> updateParameters(feature: RestFeature<T>) {
        if (mLastFeatureForParameters != feature) {
            generateParameterView(feature)
            mLastFeatureForParameters = feature
        }
    }

    private fun <T : Any> generateParameterView(feature: RestFeature<T>) {
        parametersSubscription.forEach { it.dispose() }
        parameterContainer.removeAllViews()
        feature.params.forEach { restParameter ->
            val view = activity.layoutInflater.inflate(R.layout.parameter_text, parameterContainer, false)
            view.paramTextLabel.text = restParameter.name
            val editText = view.paramTextValue
            editText.setText(restParameter.value)
//            editText.postDelayed({ editText.setSelection(restParameter.value.length) } , 50)// Update cursor at the end
            val sub = RxTextView.textChanges(editText)
                    .debounce(300, TimeUnit.MILLISECONDS)
                    .subscribe {
                        if (restParameter.value != it.toString()) {
                            restParameter.value = it.toString()
                            updateUiSubject.onNext(true)
                        }
                    }
            parametersSubscription.add(sub)

            val spinner = view.paramTextValueSpinner
            spinner.visibility = if (restParameter.suggestedValues.isEmpty()) View.GONE else View.VISIBLE
            if (restParameter.suggestedValues.isNotEmpty()) {
                spinner.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, restParameter.suggestedValues)
                RxAdapterView.itemSelections(spinner)
                        .subscribe { editText.setText(spinner.adapter.getItem(it).toString()) }
            }

            parameterContainer.addView(view)
        }
    }

    fun <T : Any> updateResult(result: RestResult<T>) {
        val dataClass = result.data.javaClass.kotlin
        resultContainer.removeAllViews()
        generateViewsRecursively(activity, result.data, dataClass, resultContainer).forEach { resultContainer.addView(it) }
    }

    private val parametersSubscription = mutableListOf<Disposable>()

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
    val newView = activity.layoutInflater.inflate(R.layout.field_object, root, false)
    newView.fieldObjectLabel.text = name
    return newView to newView.fieldObjectContainer
}
