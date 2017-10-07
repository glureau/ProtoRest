package com.glureau.protorest_core.ui.generator

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.glureau.protorest_core.R
import com.glureau.protorest_core.rest.StringArray
import com.glureau.protorest_core.ui.image.SimpleImageAdapter
import com.glureau.protorest_core.ui.image.SimpleImagePreloadModelProvider
import kotlinx.android.synthetic.main.field_recyclerview.view.*

object SimpleImageListGenerator : UiGenerator<StringArray> {
    override fun generate(activity: Activity, name: String, data: StringArray, root: ViewGroup): View {
        val newView = activity.layoutInflater.inflate(R.layout.field_recyclerview, root, false)
        newView.fieldRecyclerViewLabel.text = name
        val container = newView.fieldRecyclerViewContainer
        val modelProvider = SimpleImagePreloadModelProvider(activity, data)
        val preLoader = RecyclerViewPreloader(activity, modelProvider, ViewPreloadSizeProvider(), 5)
        container.addOnScrollListener(preLoader)
        container.setItemViewCacheSize(0)

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        container.layoutManager = layoutManager

        val simpleImageAdapter = SimpleImageAdapter(activity)
        container.adapter = simpleImageAdapter
        simpleImageAdapter.setImageUrls(data)

        return newView
    }
}

