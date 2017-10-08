package com.glureau.protorest_core.ui.generator.parameters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.glureau.protorest_core.R
import com.glureau.protorest_core.rest.RestFeature
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.parameter_text.view.*
import java.util.concurrent.TimeUnit

internal class ParameterViewManager(val activity: Activity, val parameterContainer: ViewGroup, val updateUiSubject: PublishSubject<Boolean>) {
    private val parametersSubscription = mutableListOf<Disposable>()
    private var mLastFeatureForParameters: RestFeature<out Any>? = null

    fun <T : Any> updateParametersViews(feature: RestFeature<T>) {
        if (mLastFeatureForParameters != feature) {
            update(feature)
            mLastFeatureForParameters = feature
        }
    }

    private fun <T : Any> update(feature: RestFeature<T>) {
        parametersSubscription.forEach { it.dispose() }
        parameterContainer.removeAllViews()
        feature.params.forEach { restParameter ->
            val view = activity.layoutInflater.inflate(R.layout.parameter_text, parameterContainer, false)
            view.paramTextLabel.text = restParameter.name
            val editText = view.paramTextValue
            editText.setText(restParameter.value)
            editText.post { editText.setSelection(editText.text.length) }// Update cursor at the end
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
                        .subscribe {
                            editText.setText(spinner.adapter.getItem(it).toString())
                            editText.post { editText.setSelection(editText.text.length) }// Update cursor at the end
                        }
            }

            parameterContainer.addView(view)
        }
    }
}