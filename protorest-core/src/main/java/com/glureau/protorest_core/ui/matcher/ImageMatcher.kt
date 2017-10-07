package com.glureau.protorest_core.ui.matcher

import com.glureau.protorest_core.rest.RestApi
import com.glureau.protorest_core.reflection.Reflection
import kotlin.reflect.KClass

object ImageMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = kClass.java == String::class.java && Reflection.hasFieldAnnotation(annotations, RestApi.Image::class)
}