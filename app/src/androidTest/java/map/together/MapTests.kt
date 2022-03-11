package map.together

import androidx.test.ext.junit.rules.ActivityScenarioRule
import map.together.activities.auth.LoginActivity
import map.together.mockActivities.auth.FakeLoginActivity
import map.together.screens.LoginScreen
import org.junit.Rule
import org.junit.Test

class MapTests {
    @get:Rule
    val activityRule = ActivityScenarioRule(FakeLoginActivity::class.java)
    private val loginScreen = LoginScreen()

    @Test
    fun openMap() {
        loginScreen
            .pressConfirmButton()
            .chooseFirstMap()
            .click()

    }
}