package com.glureau.geno.lib.rx

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

// Investigate to replace this by https://github.com/kizitonwose/android-disposebag
open class DisposableManager {
    private val disposables = CompositeDisposable()
    protected fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    fun dispose() {
        disposables.dispose()
    }
}