package com.glureau.geno.old.ui.matcher

import kotlin.reflect.KClass

object BooleanMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = kClass == Boolean::class
}