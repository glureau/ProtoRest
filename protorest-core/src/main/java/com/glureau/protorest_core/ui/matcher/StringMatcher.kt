package com.glureau.protorest_core.ui.matcher

import kotlin.reflect.KClass

object StringMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = kClass == String::class
}