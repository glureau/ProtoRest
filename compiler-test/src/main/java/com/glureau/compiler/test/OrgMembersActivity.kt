package com.glureau.compiler.test

import androidx.lifecycle.Observer
import androidx.room.Room
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glureau.compiler.test.api.GithubApiService
import com.glureau.compiler.test.api.dto.view.SimpleGithubUserBindingRecyclerViewAdapter
import com.glureau.compiler.test.todo.GithubUserRepository
import com.glureau.geno.db.GenoDatabase
import com.glureau.geno.lib.network.TimedCacheManagerImpl
import com.glureau.geno.lib.repository.Resource
import com.glureau.test.R
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber


class OrgMembersActivity : AppCompatActivity() {
    companion object {
        const val INTENT_PARAM_ORGANIZATION = "org"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerView: androidx.recyclerview.widget.RecyclerView = layoutInflater.inflate(R.layout.recyclerview, null) as androidx.recyclerview.widget.RecyclerView
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        setContentView(recyclerView)

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create<GithubApiService>(GithubApiService::class.java)

        val db = Room.databaseBuilder(applicationContext, GenoDatabase::class.java, "geno").build()

        val org = intent.getStringExtra(INTENT_PARAM_ORGANIZATION)
                ?: error("Keep previous org in memory or redirect to home?")

        val userRepository = GithubUserRepository(api, db.simpleGithubUserDao(), TimedCacheManagerImpl(1 * 60 * 60 * 1000))
        userRepository.getMembers(org)
                .observe(this, Observer {
                    if (it?.status == Resource.Companion.Status.LOADING
                            || it?.status == Resource.Companion.Status.SUCCESS) {
                        val users = it.data?.toMutableList() ?: mutableListOf()
                        recyclerView.adapter = SimpleGithubUserBindingRecyclerViewAdapter(users)
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