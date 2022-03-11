package map.together

import androidx.test.ext.junit.rules.ActivityScenarioRule
import map.together.mockActivities.auth.FakeLoginActivity
import map.together.screens.LoginScreen
import map.together.screens.MapsLibraryScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MapTests {
    @get:Rule
    val activityRule = ActivityScenarioRule(FakeLoginActivity::class.java)

    private val loginScreen = LoginScreen()
    private var mapsListScreen = MapsLibraryScreen()

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

    }
}