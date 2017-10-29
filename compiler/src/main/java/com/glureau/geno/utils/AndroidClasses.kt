package com.glureau.geno.utils

import com.squareup.kotlinpoet.ClassName

/**
 * Created by Greg on 22/10/2017.
 */
object AndroidClasses {
    val VIEW = ClassName("android.view", "View")
    val VIEW_GROUP = ClassName("android.view", "ViewGroup")
    val ACTIVITY = ClassName("android.app", "Activity")
    val FRAGMENT = ClassName("android.app", "Fragment")
    val IMAGE_VIEW = ClassName("android.widget", "ImageView")
    val RECYCLER_VIEW = ClassName("android.support.v7.widget", "RecyclerView")
    val RECYCLER_VIEW_HOLDER = ClassName("android.support.v7.widget", "RecyclerView", "ViewHolder")
}