package com.glureau.protorest_core.ui.matcher

import com.glureau.protorest_core.RestApi
import com.glureau.protorest_core.reflection.Reflection
import kotlin.reflect.KClass

object ImageMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = Reflection.hasAnnotation(annotations, RestApi.Image::class)
}