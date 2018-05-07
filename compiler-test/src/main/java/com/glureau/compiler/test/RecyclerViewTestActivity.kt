package com.glureau.compiler.test

import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.glureau.compiler.test.api.GithubApiService
import com.glureau.compiler.test.api.dto.view.SimpleGithubOrganizationBindingRecyclerViewAdapter
import com.glureau.compiler.test.todo.GithubOrganizationRepository
import com.glureau.geno.db.GenoDatabase
import com.glureau.geno.lib.network.TimedCacheManagerImpl
import com.glureau.geno.lib.repository.Resource
import com.glureau.test.R
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber


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

        val db = Room.databaseBuilder(applicationContext, GenoDatabase::class.java, "geno").build()

        /*
        val userRepository = GithubUserRepository(api, db.simpleGithubUserDao(), TimedCacheManagerImpl(1 * 60 * 60 * 1000))
        userRepository.getMembers("google")
                .observe(this, Observer {
                    if (it?.status == Resource.Companion.Status.LOADING
                            || it?.status == Resource.Companion.Status.SUCCESS) {
                        val users = it.data?.toMutableList() ?: mutableListOf<SimpleGithubUser>()
                        recyclerView.adapter = SimpleGithubUserBindingRecyclerViewAdapter(users)
                    } else {
                        Timber.e("Issue happened: ${it?.message}")
                        val t = it?.throwable
                        if (t != null) {
                            Timber.e(t)
                        }
                    }
                })
        */

        val userRepository = GithubOrganizationRepository(api, db.simpleGithubOrganizationDao(), TimedCacheManagerImpl(24 * 60 * 60 * 1000))
        userRepository.getAllOrganizations()
                .observe(this, Observer {
                    if (it?.status == Resource.Companion.Status.LOADING
                            || it?.status == Resource.Companion.Status.SUCCESS) {
                        val orgs = it.data?.toMutableList() ?: mutableListOf()
                        recyclerView.adapter = SimpleGithubOrganizationBindingRecyclerViewAdapter(orgs)
                    } else {
                        Timber.e("Issue happened: ${it?.message}")
                        val t = it?.throwable
                        if (t != null) {
                            Timber.e(t)
                        }
                    }
                })
    }
}
