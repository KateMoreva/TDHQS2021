package ru.ok.technopolis.training.personal.activities.settings

import android.os.Bundle
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R
import map.together.activities.BaseFragmentActivity

class AccountSettingsActivity : BaseFragmentActivity() {

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        router?.showAccountSettingsSubPage()
    }

    override fun getToolbarTitle(): String = getString(R.string.account_settings)

    override fun canOpenNavMenuFromToolbar(): Boolean = false
}