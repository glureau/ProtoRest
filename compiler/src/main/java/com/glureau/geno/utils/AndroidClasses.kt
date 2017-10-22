package com.glureau.geno.utils

import com.squareup.kotlinpoet.ClassName

/**
 * Created by Greg on 22/10/2017.
 */
object AndroidClasses {
    val ANDROID_VIEW = ClassName("android.view", "View")
    val ANDROID_VIEW_GROUP = ClassName("android.view", "ViewGroup")
    val ANDROID_ACTIVITY = ClassName("android.app", "Activity")
}