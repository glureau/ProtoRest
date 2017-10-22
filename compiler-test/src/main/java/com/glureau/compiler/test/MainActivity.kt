package com.glureau.compiler.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.glureau.geno.annotation.CustomView
import com.glureau.geno.annotation.Image
import com.glureau.test.R

@CustomView(R.layout.activity_main)
data class GithubUser(
        val login: String?,
        val name: String?,
        @Image val avatar: String?)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = GithubUser("geno", "♡♪!?", "https://www.mariowiki.com/images/thumb/5/58/Geno_SMRPG.gif/200px-Geno_SMRPG.gif")
        val viewManager = GithubUserViewManager(user)
        val view: View = GithubUserViewManager.inflate(this, null)
        viewManager.fill(view)
        setContentView(view)
    }
}
