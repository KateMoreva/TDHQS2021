package map.together.activities.settings

import map.together.R


class SettingsActivity : BaseSettingsActivity() {

    override fun getToolbarTitle(): String = getString(R.string.settings)

//    override fun getPreferencesFragment(): PreferenceFragmentCompat = SettingsFragment()
}
