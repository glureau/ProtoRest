package com.glureau.geno.annotation
import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Endpoint(@JvmField val returnType: KClass<*>) // TODO : pass the KClass as generic of the annotation, for more flexibility if required

