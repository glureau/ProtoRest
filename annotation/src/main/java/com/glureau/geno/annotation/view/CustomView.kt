package com.glureau.geno.annotation.view

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class CustomView(
        val R: KClass<*>,
        val viewName: String)
