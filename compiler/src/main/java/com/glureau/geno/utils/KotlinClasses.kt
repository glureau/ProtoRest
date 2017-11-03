package com.glureau.geno.utils

import com.squareup.kotlinpoet.ClassName

/**
 * Created by Greg on 29/10/2017.
 */
object KotlinClasses {
    val MUTABLE_LIST = ClassName("kotlin.collections", "MutableList")
    val ANNOTATION_VOLATILE = ClassName("kotlin.jvm", "Volatile")
}