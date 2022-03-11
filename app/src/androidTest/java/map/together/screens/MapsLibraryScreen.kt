package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R

class MapsLibraryScreen {

    fun pressSettingsButton() : SettingsScreen {
        Espresso.onView(ViewMatchers.withId(R.id.settings_btn))
                .perform(ViewActions.click())
        return SettingsScreen()
    }

}