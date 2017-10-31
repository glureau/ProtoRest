package com.glureau.geno.lib.image.glide

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.glureau.geno.lib.GlideApp

/**
 * Created by Greg on 31/10/2017.
 */

@BindingAdapter("glideLoadUrl")
fun setImageUri(view: ImageView, url: String?) {
    if (url != null) {
        GlideApp.with(view.context).asBitmap().load(url).into(view)
    }
    // Could use a default image directly in XML with fresco
    view.visibility = if (url == null) View.INVISIBLE else View.VISIBLE
}
