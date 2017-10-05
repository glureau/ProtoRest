package com.glureau.protorest_core.ui.generator

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.glureau.protorest_core.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.simple_image.view.*

object SimpleImageGenerator : UiGenerator {
    override fun generate(activity: Activity, name: String, data: Any, root: ViewGroup/*, additionalData: Map<Any, Any>*/): View {
        val newView = activity.layoutInflater.inflate(R.layout.simple_image, root, false)
        newView.simpleImageLabel.text = name
        Glide.with(activity).asBitmap().load(data as String).into(newView.simpleImageValue)
        return newView
    }
}