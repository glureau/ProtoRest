package com.glureau.compiler.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.glureau.compiler.test.model.GithubUser
import com.glureau.compiler.test.model.GithubUserViewManager
import com.glureau.geno.lib.rest.RestApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    val api = RestApi("https://api.github.com/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view: View = GithubUserViewManager.inflate(this, null)
        setContentView(view)

        Observable.just(1)
                .observeOn(Schedulers.io())
                .map { api.get("users/jakewharton", GithubUser::class.java) }
                .map { GithubUserViewManager(it.data as GithubUser) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it.fill(this, view) }
    }
}
