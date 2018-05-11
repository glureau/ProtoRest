package com.glureau.geno.annotation.data

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class ReferenceTo(
        val referenceClass: KClass<*>,
        val referenceField: String,
        val referenceType: ReferenceType
)
enum class ReferenceType {
    ONE_TO_ONE,
    ONE_TO_MANY,
    MANY_TO_ONE,
    MANY_TO_MANY,
}
