package com.glureau.geno.utils

import com.squareup.kotlinpoet.ClassName

/**
 * Created by Greg on 22/10/2017.
 */
object AndroidClasses {
    val CONTEXT = ClassName("android.content", "Context")

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
    val RECYCLER_VIEW = ClassName("androidx.recyclerview.widget", "RecyclerView")
    val RECYCLER_VIEW_HOLDER = ClassName("androidx.recyclerview.widget", "RecyclerView", "ViewHolder")
    fun RECYCLER_VIEW_ADAPTER(generic: String) = ClassName("androidx.recyclerview.widget", "RecyclerView", "Adapter<$generic>")

    // Room
    val DATABASE = ClassName("androidx.room", "Database")
    val ROOM = ClassName("androidx.room", "Room")
    val ROOM_DATABASE = ClassName("androidx.room", "RoomDatabase")
    val ROOM_COLUMN_INFO = ClassName("androidx.room", "ColumnInfo")
    val ROOM_ENTITY = ClassName("androidx.room", "Entity")
    val ROOM_PRIMARY_KEY = ClassName("androidx.room", "PrimaryKey")
    val ROOM_DAO = ClassName("androidx.room", "Dao")
    val ROOM_INSERT = ClassName("androidx.room", "Insert")
    val ROOM_ON_CONFLICT_STRATEGY = ClassName("androidx.room", "OnConflictStrategy")
    val ROOM_ON_CONFLICT_STRATEGY_REPLACE = ClassName("androidx.room", "OnConflictStrategy", "REPLACE")
    val ROOM_QUERY = ClassName("androidx.room", "Query")
    val ROOM_DELETE = ClassName("androidx.room", "Delete")
    val ROOM_TYPE_CONVERTERS = ClassName("androidx.room", "TypeConverters")
    val ROOM_INDEX = ClassName("androidx.room", "Index")

}