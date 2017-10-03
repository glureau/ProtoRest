package com.glureau.protorest_core.ui.matcher

import kotlin.reflect.KClass

interface TypeMatcher {
    fun match(kClass: KClass<*>, annotations: Array<out Annotation>?): Boolean
}