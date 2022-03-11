package map.together

import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import map.together.mockActivities.auth.FakeLoginActivity
import map.together.screens.LoginScreen
import map.together.screens.MapsListScreen
import org.hamcrest.core.AllOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MapTests {
    @get:Rule
    val activityRule = ActivityScenarioRule(FakeLoginActivity::class.java)

    private val loginScreen = LoginScreen()
    private var mapsListScreen = MapsListScreen()

    @Before
    fun login() {
        mapsListScreen = loginScreen
            .typeLogin("test@test.test")
            .typePassword("qwerty")
            .pressConfirmButton()
    }

    @Test
    fun openMap() {
        mapsListScreen
            .chooseFirstMap()
    }
}