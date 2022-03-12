package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R

class SettingsScreen {

    fun pressProfileButton(): ProfileScreen {
        Espresso.onView(ViewMatchers.withId(R.id.account_button_id))
                .perform(ViewActions.click())
        return ProfileScreen()
    }

}