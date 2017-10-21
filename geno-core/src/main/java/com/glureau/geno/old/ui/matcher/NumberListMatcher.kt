package com.glureau.geno.old.ui.matcher

import kotlin.reflect.KClass

object NumberListMatcher : TypeMatcher {
    override fun match(kClass: KClass<*>, annotations: Array<out Annotation>?) = kClass.java in arrayOf(DoubleArray::class.java, FloatArray::class.java, LongArray::class.java, IntArray::class.java, ShortArray::class.java, ByteArray::class.java)
}