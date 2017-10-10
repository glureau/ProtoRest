package com.glureau.protorest_core.ui.generator.values

import android.app.Activity
import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.glureau.protorest_core.reflection.Reflection
import com.glureau.protorest_core.ui.image.GlideApp
import kotlin.reflect.KClass

internal class CustomViewGenerator(val kClass: KClass<*>, @LayoutRes val resId: Int) : ValueViewGenerator<Any?> {
    override fun generate(activity: Activity, name: String, data: Any?, root: ViewGroup): View {
        val newView = activity.layoutInflater.inflate(resId, root, false) as ViewGroup

        if (data != null) {
            for (property in Reflection.properties(kClass)) {
                val value = property.call(data)
                val labelResId = activity.resources.getIdentifier("protorest_" + property.name + "_label", "id", activity.packageName)
                val labelView = newView.findViewById<TextView>(labelResId)
                if (labelView != null) {
                    labelView.text = name
                    labelView.visibility = if (value == null) View.GONE else View.VISIBLE
                }

                val valueResId = activity.resources.getIdentifier("protorest_" + property.name + "_value", "id", activity.packageName)
                val valueView = newView.findViewById<View>(valueResId)
                if (valueView != null) {
                    when (valueView) {
                        is TextView -> {
                            valueView.text = value.toString()
                        }
                        is ImageView -> {
                            GlideApp.with(activity).asBitmap().load(value.toString()).into(valueView)
                        }
                    }
                    valueView.visibility = if (value == null) View.GONE else View.VISIBLE
                }
            }
        }
        return newView
    }
}