package com.glureau.protorest_core

import android.os.Bundle
import io.reactivex.Observable

class ProtoRestActivity : DefaultFeatureActivity() {
    private lateinit var root: ProtoRestApplication<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = application as ProtoRestApplication<*>
        for ((featureName, action) in root.setup) {
            addFeatureButton(featureName, action)
        }
    }

    private fun addFeatureButton(featureName: String, action: () -> Observable<RestResult>) {
//        layoutInflater.inflate(R.layout.feature_button, this)
    }
}