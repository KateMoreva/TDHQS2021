package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R
import map.together.utils.WaitForAction

class SettingsScreen {

    fun pressProfileButton(): ProfileScreen {
        Espresso.onView(ViewMatchers.withId(R.id.account_button_id))
                .perform(ViewActions.click())
        return ProfileScreen()
    }

    fun pressCategoriesButton(): CategoriesScreen {
        Espresso.onView(ViewMatchers.withId(R.id.change_category_button_id))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(WaitForAction.waitFor(3000L))
        return CategoriesScreen()
    }

}