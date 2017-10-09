package com.glureau.protorest_core.rest.annotation

import com.glureau.protorest_core.rest.StringArray

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class EndpointParam(@JvmField val name: String, @JvmField val defaultValue: String = "", @JvmField val suggestedValues: StringArray = arrayOf())
