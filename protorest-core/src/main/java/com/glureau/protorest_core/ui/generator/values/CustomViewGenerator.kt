package com.glureau.protorest_core.ui.generator.values

import android.app.Activity
import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup

internal class CustomViewGenerator<T>(val kClass: T, @LayoutRes val resId: Int) : ValueViewGenerator<T> {
    override fun generate(activity: Activity, name: String, data: T, root: ViewGroup): View {
        val newView = activity.layoutInflater.inflate(resId, root, false)
        // Fill views automatically !
//        newView.fieldTextLabel.text = name
//        newView.fieldTextValue.text = data.toString()
        return newView
    }
}