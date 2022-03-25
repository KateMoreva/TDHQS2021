package map.together

import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.mockActivities.auth.FakeLoginActivity
import map.together.screens.LoginScreen
import org.junit.Rule

@InternalCoroutinesApi
@LargeTest
class EspressoUITests {

    @get:Rule
    val activityRule = ActivityScenarioRule(FakeLoginActivity::class.java)

    private val loginScreen = LoginScreen()

    private val isDisplayed = matches(isDisplayed())
    private val doesNotExists = doesNotExist()

}