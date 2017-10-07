package com.glureau.protorest_core.reflection

import java.lang.reflect.Method
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KotlinReflectionInternalError

object Reflection {
    fun properties(dataType: KClass<*>): List<KCallable<*>> {
        try {
            return dataType.members.filter { it is KProperty }
        } catch (e: KotlinReflectionInternalError) {
            return listOf()
        }
    }
    fun fieldAnnotations(kClass: KClass<*>, kCallable: KCallable<*>) = kClass.java.declaredFields.firstOrNull { it.name == kCallable.name }?.annotations
    fun methodAnnotations(kClass: KClass<*>, method: Method) = kClass.java.declaredMethods.firstOrNull { it.name == method.name }?.annotations
    fun hasFieldAnnotation(annotations: Array<out Annotation>?, kClass: KClass<out Annotation>) = annotations?.firstOrNull { it.annotationClass == kClass } != null
}