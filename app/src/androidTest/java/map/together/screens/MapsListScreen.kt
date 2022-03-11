package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R

class MapsListScreen {

    fun getList(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.maps_list))
    }

    fun getUpBarTitle(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.appbar_layout))
    }

    fun chooseFirstMap(): MainMenuScreen {
        Espresso.onView(ViewMatchers.withId(R.id.map_name))
            .perform(ViewActions.click())
        return MainMenuScreen()
    }
}