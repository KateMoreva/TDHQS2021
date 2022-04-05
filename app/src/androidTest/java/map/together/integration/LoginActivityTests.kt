package map.together.integration

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import map.together.R
import map.together.mockActivities.auth.FakeLoginActivity
import map.together.screens.LoginScreen
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test

@LargeTest
class LoginActivityTests {

    @get:Rule
    val activityRule = ActivityScenarioRule(FakeLoginActivity::class.java)

    private val loginScreen = LoginScreen()

    @Test
    fun loginIsCorrect() {
        loginScreen
            .pressConfirmButton()
            .getList()
            .check(
                matches(
                    allOf(
                        isDisplayed()
                    )
                )
            )

    }

    @Test
    fun loginIsIncorrect() {
        loginScreen
            .typeLogin("Wrong")
            .tryPressConfirmButton()
        onView(withText(R.string.cannot_login)).check(matches(isDisplayed()))
    }


}