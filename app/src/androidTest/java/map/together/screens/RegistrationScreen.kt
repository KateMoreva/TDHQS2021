package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R

class RegistrationScreen {

    fun getMainLabel(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.login_title_tv))
    }

    fun getErrorView(message: String): ViewInteraction {
        return Espresso.onView(ViewMatchers.withText(message))
    }

    fun typeName(login: String): RegistrationScreen {
        Espresso.onView(ViewMatchers.withId(R.id.user_name_et))
            .perform(
                ViewActions.typeText(login),
                ViewActions.closeSoftKeyboard()
            )
        return this
    }

    fun typeLogin(login: String): RegistrationScreen {
        Espresso.onView(ViewMatchers.withId(R.id.email_et))
            .perform(
                ViewActions.typeText(login),
                ViewActions.closeSoftKeyboard()
            )
        return this
    }

    fun typePassword(password: String): RegistrationScreen {
        Espresso.onView(ViewMatchers.withId(R.id.password_et))
            .perform(
                ViewActions.typeText(password),
                ViewActions.closeSoftKeyboard()
            )
        return this
    }

    fun typeConfirmPassword(password: String): RegistrationScreen {
        Espresso.onView(ViewMatchers.withId(R.id.confirm_password_et))
            .perform(
                ViewActions.typeText(password),
                ViewActions.closeSoftKeyboard()
            )
        return this
    }

    fun pressConfirmButton(): RegistrationScreen {
        Espresso.onView(ViewMatchers.withId(R.id.confirm_button))
            .perform(ViewActions.click())
        return this
    }
}
