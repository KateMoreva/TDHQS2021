package map.together

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import map.together.mockActivities.auth.FakeLoginActivity
import map.together.screens.LoginScreen
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test

@LargeTest
class FakeLoginActivityTests {

    @get:Rule
    val activityRule = ActivityScenarioRule(FakeLoginActivity::class.java)
    private val loginScreen = LoginScreen()

    @Test
    fun mainLabelExists() {
        loginScreen.getMainLabel()
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withSubstring("Map"),
                        withText(R.string.login_title)
                    )
                )
            )
    }

    @Test
    fun loginIsCorrect() {
        loginScreen
            .typeLogin("test@test.test")
            .typePassword("qwerty")
            .pressConfirmButton()
    }

}