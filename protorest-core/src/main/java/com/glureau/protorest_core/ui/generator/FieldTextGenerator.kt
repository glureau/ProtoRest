package com.glureau.protorest_core.ui.generator

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.glureau.protorest_core.R
import kotlinx.android.synthetic.main.field_text.view.*

object FieldTextGenerator : UiGenerator<Any> {
    override fun generate(activity: Activity, name: String, data: Any, root: ViewGroup): View {
        val newView = activity.layoutInflater.inflate(R.layout.field_text, root, false)
        newView.fieldTextLabel.text = name
        newView.fieldTextValue.text = data.toString()
        return newView
    }
}