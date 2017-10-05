package com.glureau.protorest_core.ui.generator

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.glureau.protorest_core.R
import com.glureau.protorest_core.StringArray
import kotlinx.android.synthetic.main.simple_object.view.*

object SimpleImageListGenerator : UiGenerator {
    override fun generate(activity: Activity, name: String, data: Any, root: ViewGroup/*, additionalData: Map<Any, Any>*/): View {
        val newView = activity.layoutInflater.inflate(R.layout.simple_object, root, false)
        newView.simpleObjectLabel.text = name
        val urls = data as StringArray // TODO : Use generics
        urls.forEach {
            val view = ImageView(activity)
            newView.simpleObjectContainer.addView(view)
            Glide.with(activity).asBitmap().load(it).into(view)
        }
        return newView
    }
}