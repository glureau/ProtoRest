package com.glureau.geno.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

/**
 * Created by Greg on 29/10/2017.
 */
object KotlinClasses {
    val MUTABLE_LIST = ClassName("kotlin.collections", "MutableList")
    fun LIST(generic: TypeName) = ParameterizedTypeName.get(ClassName("kotlin.collections", "List"), generic)
    val ANNOTATION_VOLATILE = ClassName("kotlin.jvm", "Volatile")
}