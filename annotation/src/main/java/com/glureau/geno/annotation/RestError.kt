package com.glureau.geno.annotation

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class RestError(@JvmField val errorKClass: KClass<*>)
