package com.glureau.protorest_core

import android.app.Activity
import android.app.Application
import android.view.View
import android.view.ViewGroup
import com.glureau.protorest_core.ui.UiGenerator
import com.squareup.leakcanary.LeakCanary
import io.reactivex.Observable
import timber.log.Timber


open class ProtoRestApplication<out A : RestApi>(val api: A, val title: String = "ProtoRest") : Application() {
    lateinit var setup: List<RestFeatureGroup>
    fun setup(vararg groups: RestFeatureGroup) = { setup = groups.toList()}
    fun group(name:String, vararg features: RestFeature<*>) = RestFeatureGroup(name, features.toList())
    inline fun <reified T> feature(name: String, noinline action: () -> Observable<RestResult<T>>) = RestFeature(name, action, { a, f:RestFeature<T>, r -> this.generateViews(a, f, r) })

    inline fun <reified T> generateViews(activity: Activity, feature: RestFeature<T>, root : ViewGroup): Observable<List<View>> = UiGenerator.generateViews(activity, feature, root)

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
