package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R
import map.together.utils.WaitForAction
import map.together.utils.WithIndexMatcher.withIndex
import map.together.viewholders.MapViewHolder

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
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.map_name), 0))
            .perform(ViewActions.click())
        return MainScreen()
    }

    fun chooseMapByIndex(index: Int): MainScreen {
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.map_name), index))
                .perform(ViewActions.click())
        return MainScreen()
    }

    fun createMap(): MapsLibraryScreen {
        Espresso.onView(ViewMatchers.withId(R.id.imageView))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(WaitForAction.waitFor(1000L))
        return this
    }
}