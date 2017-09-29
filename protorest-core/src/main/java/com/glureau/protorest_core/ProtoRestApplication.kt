package com.glureau.protorest_core

import android.app.Application

open class ProtoRestApplication(val api : RestApi) : Application() {
    fun <R : RestResult> table(results: List<R>) {

    }
    fun <R : RestResult> details (result : R) {
        result
    }
}
