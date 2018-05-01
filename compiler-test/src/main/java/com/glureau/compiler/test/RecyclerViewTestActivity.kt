package com.glureau.compiler.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.glureau.compiler.test.api.GithubApiService
import com.glureau.compiler.test.model.view.SimpleGithubUserBindingRecyclerViewAdapter
import com.glureau.test.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RecyclerViewTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerView: RecyclerView = layoutInflater.inflate(R.layout.recyclerview, null) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        setContentView(recyclerView)

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create<GithubApiService>(GithubApiService::class.java)

        api.getMembers("google")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val users = it.toMutableList()
                    recyclerView.adapter = SimpleGithubUserBindingRecyclerViewAdapter(users)
                }
    }
}