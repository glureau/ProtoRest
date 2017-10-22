package com.glureau.geno.lib.rest

class RestFeature<T>(val name: String,
                     val action: (params: Array<out RestParameter>) -> RestResult<T>,
        //                     val updateResultView: (Activity, RestFeature<T>, ViewGroup, ViewGroup) -> Completable,
                     val params: Array<out RestParameter>) {
//    fun updateResultView(activity: Activity, paramContainer: ViewGroup, resultContainer: ViewGroup) = updateResultView.invoke(activity, this, paramContainer, resultContainer)

    fun execute() = action.invoke(params)
}

class RestFeatureGroup(val name: String?, val features: List<RestFeature<out Any>>)