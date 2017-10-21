package com.glureau.geno.old.ui.matcher

import kotlin.reflect.KClass

object StringMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = kClass == String::class
}