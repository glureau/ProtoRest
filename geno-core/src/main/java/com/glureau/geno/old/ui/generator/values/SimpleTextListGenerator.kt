package com.glureau.geno.old.ui.generator.values

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.glureau.geno.R
import com.glureau.geno.old.rest.StringArray
import kotlinx.android.synthetic.main.field_object.view.*

internal object SimpleTextListGenerator : ValueViewGenerator<StringArray> {
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