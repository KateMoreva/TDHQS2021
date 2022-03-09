package map.together.activities.settings

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_base_fragment.*
import kotlinx.android.synthetic.main.view_appbar.*
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R
import map.together.activities.AppbarActivity

abstract class BaseSettingsActivity : AppbarActivity() {

    companion object {
        const val EMPTY_KEY = "KEY"
    }

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        println("On create 1")
        super.onCreate(savedInstanceState)
        setSettingsFragment()
        println("On create 2")
    }

    private fun setSettingsFragment() {
//        supportFragmentManager.beginTransaction().add(R.id.main_container, getPreferencesFragment()).commit()
        supportFragmentManager.executePendingTransactions()
    }

    override fun getToolbarView(): Toolbar = base_toolbar

    override fun getActivityLayoutId(): Int = R.layout.activity_base_fragment

//    abstract fun getPreferencesFragment(): PreferenceFragmentCompat

    override fun canOpenNavMenuFromToolbar(): Boolean = false

    override fun isBottomNavVisible(): Boolean = false
}
