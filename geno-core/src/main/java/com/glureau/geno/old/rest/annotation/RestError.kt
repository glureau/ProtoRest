package com.glureau.geno.old.rest.annotation

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RestError(@JvmField val errorKClass: KClass<*>)
