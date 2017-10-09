package com.glureau.protorest_core.rest.annotation

import android.support.annotation.LayoutRes

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class CustomView(@JvmField @LayoutRes val viewId: Int)
