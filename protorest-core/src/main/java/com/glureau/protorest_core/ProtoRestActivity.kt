package com.glureau.protorest_core

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class ProtoRestActivity : DefaultFeatureActivity() {
    private lateinit var root: ProtoRestApplication<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = application as ProtoRestApplication<*>

        // Open view by default
        drawer_layout.openDrawer(Gravity.START, true)

        val menu = nav_view.menu
        for (featureGroup in root.setup) {
            val subMenu = menu.addSubMenu(0, featureGroup.name.hashCode(), Menu.NONE, featureGroup.name)
            for (feature in featureGroup.features) {
                val item = subMenu.add(0, feature.name.hashCode(), Menu.NONE, featureGroup.name + " / " + feature.name)
                item.setIcon(R.drawable.account)
                item.setOnMenuItemClickListener {
                    feature.generateViews(this, mainContent)
                            .doOnSubscribe {
                                bar_title.text = "${resources.getString(R.string.app_name)} / ${featureGroup.name} / ${feature.name}"
                                loading.visibility = View.VISIBLE
                            }
                            .subscribe({ views ->
                                mainContent.removeAllViews()
                                views.forEach { mainContent.addView(it) }
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
