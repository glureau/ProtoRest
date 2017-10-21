package com.glureau.geno.old.ui.generator.values

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.glureau.geno.R
import com.glureau.geno.old.ui.image.GlideApp
import kotlinx.android.synthetic.main.field_image.view.*

internal object SimpleImageGenerator : ValueViewGenerator<String> {
    override fun generate(activity: Activity, name: String, data: String, root: ViewGroup): View {
        val newView = activity.layoutInflater.inflate(R.layout.field_image, root, false)
        newView.fieldImageLabel.text = name
        GlideApp.with(activity).asBitmap().load(data).into(newView.fieldImageValue)
        return newView
    }
}