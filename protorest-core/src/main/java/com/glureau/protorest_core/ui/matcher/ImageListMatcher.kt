package com.glureau.protorest_core.ui.matcher

import com.glureau.protorest_core.reflection.Reflection
import com.glureau.protorest_core.rest.StringArray
import com.glureau.protorest_core.rest.annotation.Image
import kotlin.reflect.KClass

object ImageListMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = kClass.java == StringArray::class.java && Reflection.hasFieldAnnotation(annotations, Image::class)
}