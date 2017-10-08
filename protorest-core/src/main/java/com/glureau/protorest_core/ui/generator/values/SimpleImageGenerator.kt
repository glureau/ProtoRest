package com.glureau.protorest_core.ui.generator.values

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.glureau.protorest_core.R
import com.glureau.protorest_core.ui.image.GlideApp
import kotlinx.android.synthetic.main.field_image.view.*

internal object SimpleImageGenerator : ValueViewGenerator<String> {
    override fun generate(activity: Activity, name: String, data: String, root: ViewGroup): View {
        val newView = activity.layoutInflater.inflate(R.layout.field_image, root, false)
        newView.fieldImageLabel.text = name
        GlideApp.with(activity).asBitmap().load(data).into(newView.fieldImageValue)
        return newView
    }
}