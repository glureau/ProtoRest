package com.glureau.geno.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName

/**
 * Created by Greg on 01/11/2017.
 */
object ReactivexClasses {
    fun MAYBE(generic: ClassName) = ParameterizedTypeName.get(ClassName("io.reactivex", "Maybe"), generic)
    fun SINGLE(generic: String) = ClassName("io.reactivex", "Single<$generic>")
    fun FLOWABLE(generic: String) = ClassName("io.reactivex", "Flowable<$generic>")
    fun OBSERVABLE(generic: String) = ClassName("io.reactivex", "Observable<$generic>")
    val COMPLETABLE = ClassName("io.reactivex", "Completable")
}