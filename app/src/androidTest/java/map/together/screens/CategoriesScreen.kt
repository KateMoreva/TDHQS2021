package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches

import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*

import map.together.R
import map.together.utils.WaitForAction
import map.together.viewholders.MapViewHolder
import org.hamcrest.CoreMatchers.*


class CategoriesScreen {

    fun getList(): ViewInteraction {
        Espresso.onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        return Espresso.onView(withId(R.id.categories_list))
    }

    fun pressChangeCategoryButton(): EditTextDialogScreen<CategoriesScreen> {
        Espresso.onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        Espresso.onView(withId(R.id.categories_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<MapViewHolder>(0, click()))
        Espresso.onView(ViewMatchers.isRoot())
                .perform(WaitForAction.waitFor(1000L))
        return EditTextDialogScreen(this)

    }

    fun isCategoryNameEqualsValue(value: String): CategoriesScreen {
        Espresso.onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        Espresso.onView(withText(value)).check(matches(isDisplayed()))
        return this
    }

    fun pressAddCategoryButton():EditTextDialogScreen<CategoriesScreen> {
        Espresso.onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(1000L))
        Espresso.onView(withId(R.id.add_category_btn))
            .perform(click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(WaitForAction.waitFor(2000L))
        return EditTextDialogScreen(this)
    }

}