package com.glureau.protorest_core.ui.matcher

import java.util.*
import kotlin.reflect.KClass

object DateMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = kClass == Date::class
}