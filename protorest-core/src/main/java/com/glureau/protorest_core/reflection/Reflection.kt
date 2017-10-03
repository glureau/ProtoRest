package com.glureau.protorest_core.reflection

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
}