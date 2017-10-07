package com.glureau.protorest_core

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.glureau.protorest_core.rest.*
import com.glureau.protorest_core.ui.DefaultFeatureActivity
import io.reactivex.Observable
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

        // Manage @RestApi.Endpoint annotations to auto-generate menu + required parameters
        root.api.javaClass.declaredMethods.forEach { endpoint ->
            val features = ArrayList<RestFeature<*>>()
            endpoint.annotations.filter { it is RestApi.Endpoint }.forEach {
                val parameters = mutableListOf<RestParameter>()
                endpoint.parameterAnnotations.forEach { annotations ->
                    val annParam = annotations.filter { it is RestApi.EndpointParam }.firstOrNull() as RestApi.EndpointParam?
                    if (annParam != null) {
                        val name = RestApi.EndpointParam::class.java.getMethod("name").invoke(annParam) as String
                        val defaultValue = RestApi.EndpointParam::class.java.getMethod("defaultValue").invoke(annParam) as String
                        parameters.add(RestParameter(name, defaultValue))
                    } else {
                        parameters.add(RestParameter())
                    }
                }
                features.add(root.feature(endpoint.name, {
                    val params = parameters.map { it.defaultValue }.toTypedArray()
                    endpoint.invoke(root.api, *params) as Observable<RestResult<Any>>
                }, *parameters.toTypedArray()))
            }
            root.addGroup(root.group("Auto", *features.toTypedArray()))
        }

        // Manual views
        for (featureGroup in root.setup) {
            val subMenu = menu.addSubMenu(0, featureGroup.name.hashCode(), Menu.NONE, featureGroup.name)
            for (feature in featureGroup.features) {
                val item = subMenu.add(0, feature.name.hashCode(), Menu.NONE, featureGroup.name + " / " + feature.name)
                item.setIcon(R.drawable.account)
                item.setOnMenuItemClickListener {
                    generateViews(featureGroup, feature)
                    drawer_layout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }
    }

    private fun generateViews(featureGroup: RestFeatureGroup, feature: RestFeature<*>) {
        feature.generateViews(this, parameterContainer, resultContainer)
                .doOnSubscribe {
                    bar_title.text = "${resources.getString(R.string.app_name)} / ${featureGroup.name} / ${feature.name}"
                    loading.visibility = View.VISIBLE
                }
                .subscribe {
                    loading.visibility = View.INVISIBLE
                    resultContainer.invalidate()
                }
    }

    // Easier to use a direct click listener
    override fun onNavigationItemSelected(item: MenuItem) = true
}
