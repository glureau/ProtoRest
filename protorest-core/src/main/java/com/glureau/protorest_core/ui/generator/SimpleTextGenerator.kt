package com.glureau.protorest_core.ui.generator

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.glureau.protorest_core.R
import kotlinx.android.synthetic.main.simple_text.view.*

object SimpleTextGenerator : UiGenerator<Any> {
    override fun generate(activity: Activity, name: String, data: Any, root: ViewGroup): View {
        val newView = activity.layoutInflater.inflate(R.layout.simple_text, root, false)
        newView.simpleTextLabel.text = name
        newView.simpleTextValue.text = data.toString()
        return newView
    }
}