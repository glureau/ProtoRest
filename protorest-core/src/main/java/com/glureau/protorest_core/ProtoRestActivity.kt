package com.glureau.protorest_core

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class ProtoRestActivity : DefaultFeatureActivity() {
    private lateinit var root: ProtoRestApplication<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = application as ProtoRestApplication<*>
        val menu = nav_view.menu
        for ((featureName, action) in root.setup) {
            val item = menu.add(0, featureName.hashCode(), Menu.NONE, featureName)
            item.setIcon(R.drawable.ic_menu_manage)
            item.setOnMenuItemClickListener {
                Toast.makeText(this, "Hello $featureName", Toast.LENGTH_LONG).show()
                drawer_layout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    // Easier to use a direct click listener
    override fun onNavigationItemSelected(item: MenuItem) = true
}
