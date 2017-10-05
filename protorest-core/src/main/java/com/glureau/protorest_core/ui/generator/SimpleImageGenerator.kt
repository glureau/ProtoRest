package com.glureau.protorest_core.ui.generator

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.glureau.protorest_core.R
import kotlinx.android.synthetic.main.simple_image.view.*

object SimpleImageGenerator : UiGenerator<String> {
    override fun generate(activity: Activity, name: String, data: String, root: ViewGroup): View {
        val newView = activity.layoutInflater.inflate(R.layout.simple_image, root, false)
        newView.simpleImageLabel.text = name
        Glide.with(activity).asBitmap().load(data).into(newView.simpleImageValue)
        return newView
    }
}