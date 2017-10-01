package com.glureau.protorest_core

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class ProtoRestActivity : DefaultFeatureActivity() {
    private lateinit var root: ProtoRestApplication<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = application as ProtoRestApplication<*>
        val menu = nav_view.menu
        for (feature in root.setup) {
            val item = menu.add(0, feature.name.hashCode(), Menu.NONE, feature.name)
            item.setIcon(R.drawable.ic_menu_manage)
            item.setOnMenuItemClickListener {
                Log.e("Activity", "menu clicked")

                feature.generateViews()
                        .subscribe({ views ->
                            Log.e("Activity", "get views : $views")
                            mainContent.removeAllViews()
                            views.reversed().forEach { mainContent.addView(it) }
                            mainContent.invalidate()
                        })

                drawer_layout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    // Easier to use a direct click listener
    override fun onNavigationItemSelected(item: MenuItem) = true
}
