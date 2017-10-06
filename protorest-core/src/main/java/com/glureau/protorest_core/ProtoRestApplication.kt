package com.glureau.protorest_core

import android.app.Application
import com.glureau.protorest_core.rest.RestApi
import com.glureau.protorest_core.rest.RestFeature
import com.glureau.protorest_core.rest.RestFeatureGroup
import com.glureau.protorest_core.rest.RestResult
import com.glureau.protorest_core.ui.UiManager
import com.squareup.leakcanary.LeakCanary
import com.squareup.moshi.JsonAdapter
import io.reactivex.Observable
import timber.log.Timber


open class ProtoRestApplication<out A : RestApi>(val api: A) : Application() {
    lateinit var setup: List<RestFeatureGroup>
    fun setup(vararg groups: RestFeatureGroup) {
        setup = groups.toList()
    }

    fun group(name: String, vararg features: RestFeature<*>) = RestFeatureGroup(name, features.toList())
    fun <T : Any> feature(name: String, action: () -> Observable<RestResult<T>>) = RestFeature(name, action, { a, f: RestFeature<T>, r -> UiManager.generateViews(a, f, r) })

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

    private fun <T> addDataAdapter(adapter: JsonAdapter<T>) {

    }

}
