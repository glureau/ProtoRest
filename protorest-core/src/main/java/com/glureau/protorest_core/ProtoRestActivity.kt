package com.glureau.protorest_core

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*

class ProtoRestActivity : DefaultFeatureActivity() {
    private lateinit var root: ProtoRestApplication<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = application as ProtoRestApplication<*>
        val menu = nav_view.menu
        for (call in root.setup) {
            val item = menu.add(0, call.name.hashCode(), Menu.NONE, call.name)
            item.setIcon(R.drawable.ic_menu_manage)
            item.setOnMenuItemClickListener {
                Toast.makeText(this, "Hello ${call.name}", Toast.LENGTH_LONG).show()
                loadApi(call)
                drawer_layout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    fun loadApi(feature: RestFeature) {
        feature.observable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ next ->
                    next.data()
                })
    }

    // Easier to use a direct click listener
    override fun onNavigationItemSelected(item: MenuItem) = true
}
