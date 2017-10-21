package com.glureau.geno.old.ui.matcher

import com.glureau.geno.old.rest.StringArray
import kotlin.reflect.KClass

object StringListMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = kClass.java == StringArray::class.java
}