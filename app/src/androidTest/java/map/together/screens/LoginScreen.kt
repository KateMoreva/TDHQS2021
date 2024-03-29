package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R
import map.together.utils.WaitForAction

class LoginScreen {

    fun getMainLabel(): ViewInteraction {
        Espresso.onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(2000L))
        return Espresso.onView(ViewMatchers.withId(R.id.login_title_tv))
    }

    fun typeLogin(login: String): LoginScreen {
        Espresso.onView(ViewMatchers.withId(R.id.email_et))
            .perform(
                    ViewActions.clearText(),
                    ViewActions.typeText(login),
                    ViewActions.closeSoftKeyboard()
            )
        return this
    }

    fun typePassword(password: String): LoginScreen {
        Espresso.onView(ViewMatchers.withId(R.id.password_et))
            .perform(
                    ViewActions.clearText(),
                    ViewActions.typeText(password),
                    ViewActions.closeSoftKeyboard()
            )
        return this
    }

    fun login(login: String, password: String): MapsLibraryScreen {
        val mapsLibraryScreen = typeLogin(login)
                .typePassword(password)
                .pressConfirmButton()
        Espresso.onView(ViewMatchers.isRoot())
                .perform(WaitForAction.waitFor(2000L))
        return mapsLibraryScreen
    }

    fun fakeLogin(): MapsLibraryScreen {
        return pressConfirmButton()
    }

    fun pressConfirmButton(): MapsLibraryScreen {
        Espresso.onView(ViewMatchers.withId(R.id.confirm_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(WaitForAction.waitFor(3000L))
        return MapsLibraryScreen()
    }

    fun tryPressConfirmButton(): LoginScreen {
        Espresso.onView(ViewMatchers.withId(R.id.confirm_button))
            .perform(ViewActions.click())
        return LoginScreen()
    }

    fun pressNotExistAccountButton(): RegistrationScreen {
        Espresso.onView(ViewMatchers.withId(R.id.not_exist_acc_tv))
            .perform(ViewActions.click())
        return RegistrationScreen()
    }

    fun isLogoVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.icon))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
