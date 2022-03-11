package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R

class LoginScreen {

    fun getMainLabel(): ViewInteraction {
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
        return typeLogin(login)
                .typePassword(password)
                .pressConfirmButton()
    }

    fun fakeLogin(): MapsLibraryScreen {
        return pressConfirmButton()
    }

    fun pressConfirmButton(): MapsLibraryScreen {
        Espresso.onView(ViewMatchers.withId(R.id.confirm_button))
            .perform(ViewActions.click())
        return MapsLibraryScreen()
    }

    fun pressNotExistAccountButton(): RegistrationScreen {
        Espresso.onView(ViewMatchers.withId(R.id.not_exist_acc_tv))
            .perform(ViewActions.click())
        return RegistrationScreen()
    }
}
