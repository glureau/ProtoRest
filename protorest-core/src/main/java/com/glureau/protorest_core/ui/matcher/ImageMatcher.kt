package com.glureau.protorest_core.ui.matcher

import com.glureau.protorest_core.reflection.Reflection
import com.glureau.protorest_core.rest.annotation.Image
import kotlin.reflect.KClass

object ImageMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = kClass.java == String::class.java && Reflection.hasFieldAnnotation(annotations, Image::class)
}