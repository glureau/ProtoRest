package com.glureau.geno.old.ui.matcher

import kotlin.reflect.KClass

object CustomClassMatcherFactory {
    fun create(expected: KClass<*>): TypeMatcher {
        return object : TypeMatcher {
            override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?): Boolean = kClass == expected
        }
    }
}
