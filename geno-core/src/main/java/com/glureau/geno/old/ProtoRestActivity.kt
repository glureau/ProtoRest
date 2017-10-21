package com.glureau.geno.old

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.view.GravityCompat
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.glureau.geno.R
import com.glureau.geno.old.rest.RestFeature
import com.glureau.geno.old.rest.RestFeatureGroup
import com.glureau.geno.old.rest.RestParameter
import com.glureau.geno.old.rest.RestResult
import com.glureau.geno.old.rest.annotation.CustomView
import com.glureau.geno.old.rest.annotation.Endpoint
import com.glureau.geno.old.rest.annotation.EndpointParam
import com.glureau.geno.old.ui.DefaultFeatureActivity
import com.glureau.geno.old.ui.UiManager
import com.glureau.geno.old.ui.generator.parameters.ParameterViewManager
import com.glureau.geno.old.ui.generator.values.*
import com.glureau.geno.old.ui.matcher.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class GenoActivity : DefaultFeatureActivity() {
    private lateinit var root: GenoApplication<*>

    private val updateUiSubject = PublishSubject.create<Boolean>()!!
    private var uiSubscription: Disposable? = null

    private lateinit var uiManager: UiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO : DAGGER 2!!
        val typeToViewGenerator = mutableListOf(
                NumberMatcher to SimpleTextGenerator,
                BooleanMatcher to SimpleTextGenerator,
                DateMatcher to SimpleTextGenerator,
                ImageListMatcher to SimpleImageListGenerator,
                ImageMatcher to SimpleImageGenerator,
                StringMatcher to SimpleTextGenerator,
                StringListMatcher to SimpleTextListGenerator,
                NumberListMatcher to SimpleTextListGenerator
        )

        root = application as GenoApplication<*>

        // Open view by default
        drawer_layout.openDrawer(Gravity.START, true)

        val menu = nav_view.menu

        // TODO : Should be moved somewhere else
        // Manage @RestApi.Endpoint annotations to auto-generate menu + required parameters
        val autoGeneratedFeatures = ArrayList<RestFeature<out Any>>()
        root.api.javaClass.declaredMethods.forEach { endpoint ->
            endpoint.annotations.filter { it is Endpoint }.forEach {
                val parameters = mutableListOf<RestParameter>()
                endpoint.parameterAnnotations.forEach { annotations ->
                    val annParam = annotations.filter { it is EndpointParam }.firstOrNull() as EndpointParam?
                    if (annParam != null) {
                        val nameBis = EndpointParam::class.java.getMethod("name").invoke(annParam) as String
                        val name = EndpointParam::name.get(annParam)
                        val defaultValue = EndpointParam::defaultValue.get(annParam)
                        val suggestedValues = EndpointParam::suggestedValues.get(annParam)
                        parameters.add(RestParameter(name, defaultValue, suggestedValues))
                    } else {
                        parameters.add(RestParameter())
                    }
                }
                autoGeneratedFeatures.add(root.feature(endpoint.name, {
                    val params = parameters.map { it.value }.toTypedArray()
                    endpoint.invoke(root.api, *params) as RestResult<Any>
                }, *parameters.toTypedArray()))
            }
        }
        val featureGroups: MutableList<RestFeatureGroup> = mutableListOf()
        featureGroups.addAll(root.setup)
        if (autoGeneratedFeatures.isNotEmpty()) {
            featureGroups.add(root.group(null, *autoGeneratedFeatures.toTypedArray()))
        }


        root.toCustomize.forEach { kClass ->
            val annotation = kClass.annotations.firstOrNull { it is CustomView } ?: error("toCustomize should only contains @CustomView(...) annotated classes.")
            val matcher = CustomClassMatcherFactory.create(kClass)
            @LayoutRes val viewId = CustomView::viewId.get(annotation as CustomView)
            typeToViewGenerator.add(0, matcher to CustomViewGenerator(kClass, viewId))
        }

        // Manual views
        for (featureGroup in featureGroups) {
            val targetMenu: Menu
            if (featureGroup.name == null) {
                targetMenu = menu
            } else {
                targetMenu = menu.addSubMenu(0, featureGroup.name.hashCode(), Menu.NONE, featureGroup.name)
            }
            for (feature in featureGroup.features) {
                val itemText = (if (featureGroup.name != null) featureGroup.name + " / " else "") + feature.name
                val item = targetMenu.add(0, feature.name.hashCode(), Menu.NONE, itemText)
                item.setIcon(R.drawable.new_box) // Todo : pass icon as a parameter
                item.setOnMenuItemClickListener {
                    selectFeature(featureGroup, feature)
                    drawer_layout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }

        uiManager = UiManager(this, ResultViewManager(this, resultContainer, typeToViewGenerator), ParameterViewManager(this, parameterContainer, updateUiSubject))
    }

    override fun onDestroy() {
        super.onDestroy()
        nav_view.menu.clear()
    }


    private fun <T : Any> selectFeature(featureGroup: RestFeatureGroup, feature: RestFeature<T>) {
        if (featureGroup.name != null) {
            bar_title.text = "${resources.getString(R.string.app_name)} / ${featureGroup.name} / ${feature.name}"
        } else {
            bar_title.text = "${resources.getString(R.string.app_name)} / ${feature.name}"
        }
        uiSubscription?.dispose()
        uiSubscription = updateUiSubject
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    uiManager.updateParameters(feature)
                    loading.visibility = View.VISIBLE
                }
                .observeOn(Schedulers.io())
                .map { feature.execute() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    uiManager.updateResult(it)
                    loading.visibility = View.INVISIBLE
                }
        updateUiSubject.onNext(true)
    }

    // Easier to use a direct click listener
    override fun onNavigationItemSelected(item: MenuItem) = true
}
