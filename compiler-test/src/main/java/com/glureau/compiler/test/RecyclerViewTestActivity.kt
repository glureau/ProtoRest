package com.glureau.compiler.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.glureau.compiler.test.model.SimpleGithubUser
import com.glureau.compiler.test.model.SimpleGithubUserBindingRecyclerViewAdapter
import com.glureau.geno.lib.rest.RestApi
import com.glureau.test.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RecyclerViewTestActivity : AppCompatActivity() {

    private val api = RestApi("https://api.github.com/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerView: RecyclerView = layoutInflater.inflate(R.layout.recyclerview, null) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        setContentView(recyclerView)

        Observable.just(1)
                .observeOn(Schedulers.io())
                .map {
                    api.get("orgs/google/members?per_page=1000", Array<SimpleGithubUser>::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val users = (it.data as Array<SimpleGithubUser>).toMutableList()
                    recyclerView.adapter = SimpleGithubUserBindingRecyclerViewAdapter(users)
                }

    }
}