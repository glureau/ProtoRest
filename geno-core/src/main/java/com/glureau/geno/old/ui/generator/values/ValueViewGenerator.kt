package com.glureau.geno.old.ui.generator.values

import android.app.Activity
import android.view.View
import android.view.ViewGroup

internal interface ValueViewGenerator<in T> {
    fun generate(activity: Activity, name: String, data: T, root: ViewGroup): View
}