package com.glureau.protorest_core.ui.generator

import android.app.Activity
import android.view.View
import android.view.ViewGroup

interface UiGenerator {
    fun generate(activity: Activity, name: String, data: Any, root: ViewGroup, additionalData: Map<Any, Any>): View
}