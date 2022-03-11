package map.together

import androidx.test.ext.junit.rules.ActivityScenarioRule
import map.together.activities.auth.LoginActivity
import map.together.screens.LoginScreen
import org.junit.Rule
import org.junit.Test

class MapTests {
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)
    private val loginScreen = LoginScreen()

    private val testUserEmail = "test@test.test"
    private val testUserPassword = "qwerty"


    @Test
    fun openMap() {
        loginScreen
            .typeLogin(testUserEmail)
            .typePassword(testUserPassword)
            .pressConfirmButton()
            .chooseFirstMap()
            .click()

    }
}