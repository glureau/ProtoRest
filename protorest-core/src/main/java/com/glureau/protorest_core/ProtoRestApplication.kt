package com.glureau.protorest_core

import android.app.Application
import io.reactivex.Observable

open class ProtoRestApplication<T : RestApi>(val api: T) : Application() {
    lateinit var setup: Map<String, () -> Observable<RestResult>>

}
