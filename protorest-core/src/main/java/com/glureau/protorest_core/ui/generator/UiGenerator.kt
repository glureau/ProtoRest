package com.glureau.protorest_core.ui.generator

import android.app.Activity
import android.view.View
import android.view.ViewGroup

interface UiGenerator<T> {
    fun generate(activity: Activity, name: String, data: T, root: ViewGroup): View
}