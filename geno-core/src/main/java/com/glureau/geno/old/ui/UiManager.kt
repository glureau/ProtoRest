package com.glureau.geno.old.ui

import android.app.Activity
import com.glureau.geno.old.rest.RestFeature
import com.glureau.geno.old.rest.RestResult
import com.glureau.geno.old.ui.generator.parameters.ParameterViewManager
import com.glureau.geno.old.ui.generator.values.ResultViewManager

internal class UiManager(val activity: Activity, val resultViewManager: ResultViewManager, val parameterViewManager: ParameterViewManager) {

    fun <T : Any> updateParameters(feature: RestFeature<T>) {
        parameterViewManager.updateParametersViews(feature)
    }

    fun <T : Any> updateResult(result: RestResult<T>) {
        resultViewManager.updateResultView(result.data, result.data.javaClass.kotlin)
    }
}
