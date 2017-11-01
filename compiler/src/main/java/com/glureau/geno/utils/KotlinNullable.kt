package com.glureau.geno.utils

import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.VariableElement
import kotlin.reflect.KClass

/**
 * Created by Greg on 01/11/2017.
 */
object KotlinNullable {
    fun typeName(field: VariableElement) =
            if (field.annotationMirrors.firstOrNull { it.toString().contains(".Nullable") } == null)
                field.asType().asTypeName()
            else
                field.asType().asTypeName().asNullable()

    fun asTypeName(kClass: KClass<*>, nullable: Boolean) =
            if (nullable)
                kClass.asTypeName().asNullable()
            else
                kClass.asTypeName()
}