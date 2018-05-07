package com.glureau.geno.annotation.data

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class ReferenceTo(
        val referenceClass: KClass<*>,
        val referenceField: String
)
