package com.glureau.geno.old.ui.matcher

import com.glureau.geno.old.reflection.Reflection
import com.glureau.geno.old.rest.StringArray
import com.glureau.geno.old.rest.annotation.Image
import kotlin.reflect.KClass

object ImageListMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = kClass.java == StringArray::class.java && Reflection.hasFieldAnnotation(annotations, Image::class)
}