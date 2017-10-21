package com.glureau.geno.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class EndpointParam(
        @JvmField val name: String,
        @JvmField val defaultValue: String = "",
        @JvmField val suggestedValues: Array<String> = arrayOf())
