package com.glureau.protorest_core.ui

import android.app.Activity
import com.glureau.protorest_core.rest.RestFeature
import com.glureau.protorest_core.rest.RestResult
import com.glureau.protorest_core.ui.generator.parameters.ParameterViewManager
import com.glureau.protorest_core.ui.generator.values.ResultViewManager

internal class UiManager(val activity: Activity, val resultViewManager: ResultViewManager, val parameterViewManager: ParameterViewManager) {

    fun <T : Any> updateParameters(feature: RestFeature<T>) {
        parameterViewManager.updateParametersViews(feature)
    }

    fun <T : Any> updateResult(result: RestResult<T>) {
        resultViewManager.updateResultView(result.data, result.data.javaClass.kotlin)
    }
}
