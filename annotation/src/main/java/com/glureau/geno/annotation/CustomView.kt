package com.glureau.geno.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class CustomView(@JvmField val viewId: Int)
