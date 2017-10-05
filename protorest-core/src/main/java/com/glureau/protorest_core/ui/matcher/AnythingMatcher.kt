package com.glureau.protorest_core.ui.matcher

import kotlin.reflect.KClass

object AnythingMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = true
}