package com.glureau.protorest_core

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class ProtoRestActivity : DefaultFeatureActivity() {
    private lateinit var root: ProtoRestApplication<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = application as ProtoRestApplication<*>
        bar_title.text = root.title

        // Only to update once the nav_header title...
        drawer_layout.addDrawerListener(object: DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) { }
            override fun onDrawerSlide(drawerView: View?, slideOffset: Float) { }
            override fun onDrawerClosed(drawerView: View?) { }
            override fun onDrawerOpened(drawerView: View?) { nav_header_title.text = root.title }
        })

        val menu = nav_view.menu
        for (feature in root.setup) {
            val item = menu.add(0, feature.name.hashCode(), Menu.NONE, feature.name)
            item.setIcon(R.drawable.ic_menu_manage)
            item.setOnMenuItemClickListener {

                feature.generateViews(this, mainContent)
                        .doOnSubscribe { bar_title.text = "${root.title} / ${feature.name}" }
                        .subscribe({ views ->
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
