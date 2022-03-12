package map.together.screens

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import map.together.R
import map.together.utils.WaitForAction
import map.together.utils.WithIndexMatcher.withIndex
import org.hamcrest.Matcher


class MainScreen {
    fun zoomIn(): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        for (i in (0..8)) {
            onView(ViewMatchers.isRoot())
                .perform(WaitForAction.waitFor(1000L))
            onView(ViewMatchers.withId(R.id.zoom_in_id))
                .perform(ViewActions.click())
        }
        return this
    }

    fun clickOnMap(): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        onView(ViewMatchers.withId(R.id.mapview))
            .perform(ViewActions.click())
        return this
    }

    fun getAddress(): String {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        return getText(ViewMatchers.withId(R.id.category_on_tap_adress_id)).toString()
    }

    fun getCategoryName(): String {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        return getText(ViewMatchers.withId(R.id.category_on_tap_name_id)).toString()
    }

    fun getUserPlaceName(): String {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        return getText(ViewMatchers.withId(R.id.category_on_tap_place_name_id)).toString()
    }

    fun getUserAdditionalPlaceData(): String {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        return getText(ViewMatchers.withId(R.id.category_on_tap_place_description_id)).toString()
    }

    fun getUserName(): String {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        return getText(ViewMatchers.withId(R.id.category_on_tap_user_name_id)).toString()
    }

    fun clickSavePlace(): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        onView(ViewMatchers.withId(R.id.category_on_tap_adress_id))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.category_on_tap_save_changes_id))
            .perform(ViewActions.click())
        return this
    }

    fun openLayers(): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        onView(ViewMatchers.withId(R.id.layers_btn))
            .perform(ViewActions.click())
        return this
    }

    fun createLayer(): MainScreen {
        onView(ViewMatchers.withId(R.id.resizable_layers_menu))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.add_layer_btn))
            .perform(ViewActions.click())
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        return this
    }

    fun checkLayerCreated(index: Int): MainScreen {
        onView(ViewMatchers.withId(R.id.layers_list))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
        onView(withIndex(ViewMatchers.withId(R.id.layers_list), index))
            .check(matches(isDisplayed()))
        return this
    }

    fun getText(matcher: Matcher<View?>?): String? {
        val stringHolder = arrayOf<String?>(null)
        onView(matcher).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "getting text from a TextView"
            }

            override fun perform(uiController: UiController, view: View) {
                val tv = view as TextView //Save, because of check in getConstraints()
                stringHolder[0] = tv.text.toString()
            }
        })
        return stringHolder[0]
    }
}