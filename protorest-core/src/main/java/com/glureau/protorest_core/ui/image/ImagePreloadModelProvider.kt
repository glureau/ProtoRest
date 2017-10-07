package com.glureau.protorest_core.ui.image

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.*
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.glureau.protorest_core.R
import com.glureau.protorest_core.rest.StringArray
import java.io.InputStream


data class SimpleImage(val url: String)

class SimpleImagePreloadModelProvider(context: Context, val urls: StringArray) : ListPreloader.PreloadModelProvider<SimpleImage> {
    private val glideRequests = GlideApp.with(context)
    override fun getPreloadRequestBuilder(item: SimpleImage): RequestBuilder<*> = glideRequests.asDrawable().load(item.url)

    override fun getPreloadItems(position: Int): List<SimpleImage> {
        val url = urls.get(position)
        if (url.isEmpty()) {
            return emptyList()
        }
        return listOf(SimpleImage(url))
    }
}

class SimpleImageAdapter(val activity: Activity) : RecyclerView.Adapter<SimpleImageViewHolder>() {

    private val preloadSizeProvider = ViewPreloadSizeProvider<SimpleImage>()
    private var images: List<SimpleImage> = mutableListOf()
    private val glideRequests = GlideApp.with(activity)
    private val fullRequest = glideRequests.asDrawable().centerCrop().placeholder(ColorDrawable(Color.GRAY))
    private val thumbRequest = glideRequests.asDrawable().centerCrop().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.DATA).transition(withCrossFade())

    fun setImageUrls(imgs: StringArray) {
        images = imgs.map { SimpleImage(it) }
        notifyDataSetChanged()
    }

    override fun getItemCount() = images.count()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SimpleImageViewHolder {
        val view = activity.layoutInflater.inflate(R.layout.image, parent, false)
        val vh = SimpleImageViewHolder(view)
        preloadSizeProvider.setView(vh.imageView)
        return vh
    }

    override fun onBindViewHolder(holder: SimpleImageViewHolder, position: Int) {
        val current = images[position]
        fullRequest.load(current)
                .thumbnail(thumbRequest.load(current))
                .into(holder.imageView)
    }

    override fun getItemId(position: Int) = RecyclerView.NO_ID
}

class SimpleImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById<ImageView>(R.id.imageValue)
}

class SimpleImageModelLoader(build: ModelLoader<GlideUrl, InputStream>, modelCache: ModelCache<SimpleImage, GlideUrl>) : BaseGlideUrlLoader<SimpleImage>(build, modelCache) {
    override fun getUrl(model: SimpleImage, width: Int, height: Int, options: Options?) = model.url

    override fun handles(model: SimpleImage?) = true

    class Factory : ModelLoaderFactory<SimpleImage, InputStream> {
        private val modelCache = ModelCache<SimpleImage, GlideUrl>(100)

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<SimpleImage, InputStream> {
            return SimpleImageModelLoader(multiFactory.build(GlideUrl::class.java, InputStream::class.java), modelCache)
        }

        override fun teardown() {
        }

    }
}