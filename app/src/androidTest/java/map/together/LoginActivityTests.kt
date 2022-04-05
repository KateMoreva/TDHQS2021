package map.together

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import map.together.activities.auth.LoginActivity
import map.together.screens.LoginScreen
import org.hamcrest.core.AllOf
import org.junit.Rule
import org.junit.Test

@LargeTest
class LoginActivityTests {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    private val loginScreen = LoginScreen()

    @Test
    fun mainLabelExists() {
        loginScreen.getMainLabel()
            .check(
                ViewAssertions.matches(
                    AllOf.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withSubstring("Map"),
                        ViewMatchers.withText(R.string.login_title)
                    )
                )
            )
    }

    @Test
    fun loginIsCorrect() {
        loginScreen
            .pressConfirmButton()
            .getList()
            .check(
                ViewAssertions.matches(
                    AllOf.allOf(
                        ViewMatchers.isDisplayed()
                    )
                )
            )

    }

    @Test
    fun loginIsIncorrect() {
        loginScreen
            .typeLogin("Wrong")
            .tryPressConfirmButton()
        Espresso.onView(ViewMatchers.withText(R.string.cannot_login))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}