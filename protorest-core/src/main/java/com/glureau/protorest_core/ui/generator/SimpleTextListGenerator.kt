package com.glureau.protorest_core.ui.generator

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.glureau.protorest_core.R
import com.glureau.protorest_core.rest.StringArray
import kotlinx.android.synthetic.main.field_object.view.*

object SimpleTextListGenerator : UiGenerator<StringArray> {
    override fun generate(activity: Activity, name: String, data: StringArray, root: ViewGroup/*, additionalData: Map<Any, Any>*/): View {
        val newView = activity.layoutInflater.inflate(R.layout.field_object, root, false)
        newView.fieldObjectLabel.text = name
        data.forEach {
            val view = TextView(activity)
            view.text = it
            newView.fieldObjectContainer.addView(view)
        }
        return newView
    }
}