package com.glureau.protorest_core

import android.app.Application
import com.glureau.protorest_core.rest.*
import com.glureau.protorest_core.ui.UiManager
import com.squareup.leakcanary.LeakCanary
import io.reactivex.Observable
import timber.log.Timber


open class ProtoRestApplication<out A : RestApi>(val api: A) : Application() {
    lateinit var setup: MutableList<RestFeatureGroup>
    fun setup(vararg groups: RestFeatureGroup) {
        setup = groups.toMutableList()
    }

    internal fun addGroup(group: RestFeatureGroup) {
        setup.add(group)
    }

    fun group(name: String, vararg features: RestFeature<*>) = RestFeatureGroup(name, features.toList())

    fun <T : Any> feature(name: String,
                          action: (params: Array<out RestParameter<*>>) -> Observable<RestResult<T>>,
                          vararg params: RestParameter<*>): RestFeature<T> {

        return RestFeature(name,
                action,
                { a, f: RestFeature<T>, r -> UiManager.generateViews(a, f, r) },
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
