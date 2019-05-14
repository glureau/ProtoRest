package com.glureau.geno.annotation.data

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class OneToOne(
        val referenceClassA: KClass<*>,
        val referenceClassB: KClass<*>
)