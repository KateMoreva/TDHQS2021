package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R
import map.together.utils.WaitForAction
import map.together.viewholders.MapViewHolder

class MapsLibraryScreen {

    fun pressSettingsButton(): SettingsScreen {
        Espresso.onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(2000L))
        Espresso.onView(ViewMatchers.withId(R.id.settings_btn))
            .perform(click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(WaitForAction.waitFor(3000L))
        return SettingsScreen()
    }

    fun getList(): ViewInteraction {
        Espresso.onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(2000L))
        return Espresso.onView(ViewMatchers.withId(R.id.maps_list))
    }

    fun getUpBarTitle(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.appbar_layout))
    }

    fun chooseMapByIndex(index: Int): MainScreen {
        Espresso.onView(ViewMatchers.withId(R.id.maps_list))
                .perform(actionOnItemAtPosition<MapViewHolder>(index, click()))
        return MainScreen()
    }

    fun createMap(): MapsLibraryScreen {
        Espresso.onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(2500L))
        Espresso.onView(ViewMatchers.withId(R.id.imageView))
            .perform(click())
        Espresso.onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(2000L))
        return this
    }
}