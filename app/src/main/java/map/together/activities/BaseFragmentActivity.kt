package map.together.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.yandex.mapkit.MapKitFactory
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R
import map.together.fragments.BaseFragment
import map.together.lifecycle.Page
import map.together.lifecycle.Page.Companion.PAGE_KEY
import map.together.utils.logger.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

open class BaseFragmentActivity : AppbarActivity() {

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragment: Page.Fragment? = intent.getSerializableExtra(PAGE_KEY) as? Page.Fragment

        fragment?.let {
            setSupportingFragment(it.clazz)
        }
    }

    private fun setSupportingFragment(clazz: KClass<out BaseFragment>) {
        val supportingFragment = clazz.createInstance()
        Logger.d(this, "Set fragment ${supportingFragment::class.simpleName}")
        supportFragmentManager.beginTransaction().add(R.id.main_container, supportingFragment).commit()
        supportFragmentManager.executePendingTransactions()
    }

    fun setFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_container, fragment)
        ft.addToBackStack(null)
        ft.setCustomAnimations(
            android.R.animator.fade_in, android.R.animator.fade_out
        )
        ft.commit()
    }

    fun setPrevFragment() {
        val fm = supportFragmentManager
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
        } else {
            Logger.e(this, "Fragment stack is empty!")
        }
    }
//
//    override fun onStop() {
//        // Activity onStop call must be passed to both MapView and MapKit instance.
//        MapKitFactory.getInstance().onStop()
//        super.onStop()
//    }
//
//    override fun onStart() {
//        // Activity onStart call must be passed to both MapView and MapKit instance.
//        super.onStart()
//        MapKitFactory.getInstance().onStart()
//    }

    override fun getActivityLayoutId() = R.layout.activity_base_fragment

    override fun getToolbarView(): Toolbar = base_toolbar
}
