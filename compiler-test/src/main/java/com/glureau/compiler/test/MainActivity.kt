package com.glureau.compiler.test

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.glureau.geno.annotation.CustomView
import com.glureau.geno.annotation.Image
import com.glureau.test.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    @CustomView(R.layout.activity_main)
    data class GithubUser(
            val login: String?,
            val name: String?, @Image val avatar_url: String?,
            val created_at: Date?, val html_url: String?, val company: String?, val location: String?,
            val followers: Int?, val following: Int?, val public_repos: Int?, val public_gists: Int?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hello_world.text = Greeter("me").greet()
    }
}
