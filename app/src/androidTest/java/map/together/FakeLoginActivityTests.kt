package map.together

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.mockActivities.auth.FakeLoginActivity
import map.together.screens.LoginScreen
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test

@InternalCoroutinesApi
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
    fun loginIsInCorrect() {
        loginScreen
            .typePassword("wrong")
            .tryPressConfirmButton()
            .getMainLabel()
            .check(
                matches(
                    allOf(
                        isDisplayed()
                    )
                )
            )

    }

}