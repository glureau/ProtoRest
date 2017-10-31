package com.glureau.compiler.test

import android.app.Application
import com.glureau.test.BuildConfig
import timber.log.Timber

/**
 * Created by Greg on 31/10/2017.
 */
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}