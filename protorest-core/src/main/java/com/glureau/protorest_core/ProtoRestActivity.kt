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
        drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {}
            override fun onDrawerClosed(drawerView: View?) {}
            override fun onDrawerOpened(drawerView: View?) {
                nav_header_title.text = root.title
            }
        })

        val menu = nav_view.menu
        for (featureGroup in root.setup) {
            val subMenu = menu.addSubMenu(0, featureGroup.name.hashCode(), Menu.NONE, featureGroup.name)
            for (feature in featureGroup.features) {
                val item = subMenu.add(0, feature.name.hashCode(), Menu.NONE, featureGroup.name + " / " +feature.name)
                item.setIcon(R.drawable.account)
                item.setOnMenuItemClickListener {
                    feature.generateViews(this, mainContent)
                            .doOnSubscribe {
                                bar_title.text = "${root.title} / ${featureGroup.name} / ${feature.name}"
                                loading.visibility = View.VISIBLE
                            }
                            .subscribe({ views ->
                                mainContent.removeAllViews()
                                views.reversed().forEach { mainContent.addView(it) }
                                loading.visibility = View.INVISIBLE
                                mainContent.invalidate()
                            })

                    drawer_layout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }
    }

    // Easier to use a direct click listener
    override fun onNavigationItemSelected(item: MenuItem) = true
}
