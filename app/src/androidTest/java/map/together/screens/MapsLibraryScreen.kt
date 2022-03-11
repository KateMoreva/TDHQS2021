package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R

class MapsLibraryScreen {

    fun pressSettingsButton(): SettingsScreen {
        Espresso.onView(ViewMatchers.withId(R.id.settings_btn))
            .perform(ViewActions.click())
        return SettingsScreen()
    }

    fun getList(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.maps_list))
    }

    fun getUpBarTitle(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.appbar_layout))
    }

    fun chooseFirstMap(): MainScreen {
        Espresso.onView(ViewMatchers.withId(R.id.open_map_btn))
            .perform(ViewActions.click())
        return MainScreen()
    }
}