package com.glureau.protorest_core.ui.matcher

import com.glureau.protorest_core.reflection.Reflection
import com.glureau.protorest_core.rest.RestApi
import com.glureau.protorest_core.rest.StringArray
import kotlin.reflect.KClass

object ImageListMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = kClass.java == StringArray::class.java && Reflection.hasAnnotation(annotations, RestApi.Image::class)
}