package com.glureau.protorest_core.ui.matcher

import kotlin.reflect.KClass

object CustomClassMatcherFactory {
    fun create(expected: KClass<*>): TypeMatcher {
        return object : TypeMatcher {
            override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?): Boolean = kClass == expected
        }
    }
}
