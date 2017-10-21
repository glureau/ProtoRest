package com.glureau.geno.old.rest.annotation

import com.glureau.geno.old.rest.StringArray

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class EndpointParam(
        @JvmField val name: String,
        @JvmField val defaultValue: String = "",
        @JvmField val suggestedValues: StringArray = arrayOf())
