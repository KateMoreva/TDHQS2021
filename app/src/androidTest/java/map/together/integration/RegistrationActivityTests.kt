package map.together.integration

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import map.together.R
import map.together.mockActivities.auth.FakeLoginActivity
import map.together.screens.LoginScreen
import org.hamcrest.core.AllOf
import org.junit.Rule
import org.junit.Test


@LargeTest
class RegistrationActivityTests {
    @get:Rule
    val activityRule = ActivityScenarioRule(FakeLoginActivity::class.java)

    private val loginScreen = LoginScreen()

    @Test
    fun registrationOpen() {
        loginScreen
            .pressNotExistAccountButton()
            .getMainLabel()
            .check(
                matches(
                    AllOf.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(R.string.registration)
                    )
                )
            )
    }

    @Test
    fun registrationWrongEmail() {
        loginScreen
            .pressNotExistAccountButton()
            .typeName("user")
            .typeLogin("test@test.test874^%&%^*^(^%#")
            .typePassword("qqwery1")
            .typeConfirmPassword("qqwery1")
            .tryPressConfirmButton()
            .getErrorMessage()
            .check(
                matches(
                    isDisplayed()
                )
            )
    }


}