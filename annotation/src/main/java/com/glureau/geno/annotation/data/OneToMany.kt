package com.glureau.geno.annotation.data

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class OneToMany(
        val referenceClassA: KClass<*>,
        val referenceClassB: KClass<*>
)