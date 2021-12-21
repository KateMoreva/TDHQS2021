package map.together.lifecycle

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import map.together.activities.BaseFragmentActivity
import map.together.lifecycle.Page.Companion.MAP_ID_KEY
import map.together.lifecycle.Page.Companion.LAYERS_IDS
import map.together.lifecycle.Page.Companion.PAGE_KEY
import map.together.utils.logger.Logger
import kotlin.reflect.full.createInstance

class Router(private val activity: Activity) {

    fun showLoginPage() {
        showPage(Page.Activity.Login)
    }

    // here is a local map id! If you want to find the serverId you need to get MapEntity from the database
    fun showMainPage(mapId: Long) {
        val bundle = Bundle(1)
        bundle.putLong(MAP_ID_KEY, mapId)
        showPage(Page.Activity.Main, bundle)
    }

    fun showRegistrationPage() {
        showPage(Page.Activity.Registration)
    }

    fun showSettingsPage() {
        showPage(Page.Fragment.Settings)
    }

    fun showSettingsProfilePage() {
        showPage(Page.Fragment.SettingsProfile)
    }

    fun showSettingsAboutPage() {
        showPage(Page.Fragment.SettingsAbout)
    }

    fun showSettingsCategoriesPage() {
        showPage(Page.Fragment.SettingsCategories)
    }

    fun showPlacesPage(layersIds: LongArray) {
        val bundle = Bundle(1)
        bundle.putLongArray(LAYERS_IDS, layersIds)
        showPage(Page.Fragment.Places, bundle)
    }

    fun showMapsLibraryPage() {
        showPage(Page.Fragment.MapsLibrary)
    }


    private fun showPage(page: Page, bundle: Bundle? = null) {
        Logger.d(this, "showPage $page")
        when (page) {
            is Page.Activity -> {
                println("Activity")
                showActivity(page, bundle)
            }
            is Page.Fragment -> {
                println("Fragment")
                if (activity is BaseFragmentActivity) {
                    println("(BaseFrAct)")
                    replaceFragment(page, bundle)
                } else {
                    println("(ActWithFr)")
                    showActivityWithFragment(page, bundle)
                }
            }
        }
    }


    private fun showActivity(page: Page.Activity, bundle: Bundle?) {
        val intent = Intent(activity, page.clazz.java)
        bundle?.let { intent.putExtras(it) }
        activity.startActivity(intent)
    }

    private fun showActivityWithFragment(page: Page.Fragment, bundle: Bundle?) {
        val intent = Intent(activity, BaseFragmentActivity::class.java)
        intent.putExtra(PAGE_KEY, page)
        bundle?.let { intent.putExtras(it) }
        activity.startActivity(intent)
    }

    private fun replaceFragment(page: Page.Fragment, bundle: Bundle?) {
        bundle?.let { activity.intent.putExtras(it) }
        (activity as? BaseFragmentActivity)?.setFragment(page.clazz.createInstance())
    }

    fun goToPrevFragment() {
        (activity as? BaseFragmentActivity)?.setPrevFragment()
    }
}