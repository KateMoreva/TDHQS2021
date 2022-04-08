package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R

class ProfileScreen {

    fun pressChangeUsernameButton(): EditTextDialogScreen<ProfileScreen> {
        Espresso.onView(ViewMatchers.withId(R.id.change_user_name_button_id))
                .perform(ViewActions.click())
        return EditTextDialogScreen(this)
    }

    fun isUsernameEqualsValue(value: String): ProfileScreen {
        Espresso.onView(ViewMatchers.withId(R.id.user_name_text_field_id))
                .check(matches(ViewMatchers.withText(value)));
        return this
    }

    fun pressLogoutButton(): LoginScreen {
        Espresso.onView(ViewMatchers.withId(R.id.logout_button_id))
                .perform(ViewActions.click())
        return LoginScreen()
    }

}