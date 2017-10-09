package com.glureau.protorest_core

import android.app.Application
import com.glureau.protorest_core.rest.*
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber
import kotlin.reflect.KClass

// TODO : Remove the toCustomize and use reflection/code generation to get classes by annotations
open class ProtoRestApplication<out A : RestApi>(val api: A, val toCustomize: Array<KClass<*>> = emptyArray()) : Application() {
    var setup: List<RestFeatureGroup> = listOf()
    fun setup(vararg groups: RestFeatureGroup) {
        setup = groups.toList()
    }

    /**
     * @param name set to null to insert in the main menu directly
     */
    fun group(name: String?, vararg features: RestFeature<out Any>) = RestFeatureGroup(name, features.toList())

    fun <T : Any> feature(name: String,
                          action: (params: Array<out RestParameter>) -> RestResult<T>,
                          vararg params: RestParameter): RestFeature<T> {

        return RestFeature(name,
                action,
                params)
    }

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}
