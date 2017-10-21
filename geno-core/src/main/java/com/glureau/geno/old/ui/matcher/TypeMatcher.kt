package com.glureau.geno.old.ui.matcher

import kotlin.reflect.KClass

interface TypeMatcher {
    fun match(kClass: KClass<*>, annotations: Array<out Annotation>?): Boolean
}