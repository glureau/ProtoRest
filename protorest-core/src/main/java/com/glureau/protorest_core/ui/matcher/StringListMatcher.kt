package com.glureau.protorest_core.ui.matcher

import com.glureau.protorest_core.rest.StringArray
import kotlin.reflect.KClass

object StringListMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = kClass.java == StringArray::class.java
}