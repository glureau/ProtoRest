package com.glureau.protorest_core

import android.app.Application

open class ProtoRestApplication<T : RestApi>(val api: T) : Application() {
    lateinit var setup: List<RestFeature>

}
