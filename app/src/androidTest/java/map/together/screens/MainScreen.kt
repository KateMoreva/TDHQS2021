package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R

class MainScreen {
    fun click(): MainScreen {
        Espresso.onView(ViewMatchers.withId(R.id.map_name))
            .perform(ViewActions.click())
        return MainScreen()
    }

    fun openLayers(): MainScreen {
        Espresso.onView(ViewMatchers.withId(R.id.layers_btn))
            .perform(ViewActions.click())
        return MainScreen()
    }
}