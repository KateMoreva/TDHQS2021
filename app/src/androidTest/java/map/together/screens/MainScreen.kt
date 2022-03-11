package map.together.screens

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import map.together.R
import map.together.utils.WaitForAction
import map.together.utils.WithIndexMatcher.withIndex

class MainScreen {
    fun click(): MainScreen {
        Espresso.onView(ViewMatchers.withId(R.id.map_name))
            .perform(ViewActions.click())
        return this
    }

    fun openLayers(): MainScreen {
        Espresso.onView(ViewMatchers.withId(R.id.layers_btn))
            .perform(ViewActions.click())
        return this
    }

    fun createLayer(): MainScreen {
        Espresso.onView(ViewMatchers.withId(R.id.add_layer_btn))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        return this
    }

    fun checkLayerCreated(index: Int): MainScreen {
        Espresso.onView(ViewMatchers.withId(R.id.layers_list))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.layers_list), index))
                .check(matches(isDisplayed()))
        return this
    }
}