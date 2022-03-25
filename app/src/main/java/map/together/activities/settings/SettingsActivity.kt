package map.together.activities.settings

import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R


@InternalCoroutinesApi
class SettingsActivity : BaseSettingsActivity() {

    override fun getToolbarTitle(): String = getString(R.string.settings)

//    override fun getPreferencesFragment(): PreferenceFragmentCompat = SettingsFragment()
}
