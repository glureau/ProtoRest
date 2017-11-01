package com.glureau.geno.utils

import com.squareup.kotlinpoet.ClassName

/**
 * Created by Greg on 22/10/2017.
 */
object AndroidClasses {
    // View
    val VIEW = ClassName("android.view", "View")
    val VIEW_GROUP = ClassName("android.view", "ViewGroup")
    val LAYOUT_INFLATER = ClassName("android.view", "LayoutInflater")

    // App
    val ACTIVITY = ClassName("android.app", "Activity")
    val FRAGMENT = ClassName("android.app", "Fragment")

    // Widget
    val IMAGE_VIEW = ClassName("android.widget", "ImageView")

    // Support v7
    val RECYCLER_VIEW = ClassName("android.support.v7.widget", "RecyclerView")
    val RECYCLER_VIEW_HOLDER = ClassName("android.support.v7.widget", "RecyclerView", "ViewHolder")
    fun RECYCLER_VIEW_ADAPTER(generic: String) = ClassName("android.support.v7.widget", "RecyclerView", "Adapter<$generic>")

    // Room
    val ROOM_COLUMN_INFO = ClassName("android.arch.persistence.room", "ColumnInfo")
    val ROOM_ENTITY = ClassName("android.arch.persistence.room", "Entity")
    val ROOM_PRIMARY_KEY = ClassName("android.arch.persistence.room", "PrimaryKey")
}