package map.together.integration

import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import map.together.mockActivities.auth.FakeLoginActivity
import map.together.screens.LoginScreen
import map.together.screens.MapsLibraryScreen
import org.hamcrest.core.AllOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MapActivityTests {
    @get:Rule
    val activityRule = ActivityScenarioRule(FakeLoginActivity::class.java)

    private val loginScreen = LoginScreen()
    private var mapsListScreen = MapsLibraryScreen()

    private val email = "test@test.test"
    private val password = "qwerty"

    @Before
    fun login() {
        mapsListScreen = loginScreen
            .typeLogin(email)
            .typePassword(password)
            .pressConfirmButton()
    }

    @Test
    fun openPlaceMenu() {
        val placeAddress = mapsListScreen
            .chooseMapByIndex(0)
            .zoomIn()
            .clickOnMap()
            .getAddress()
        Assert.assertTrue(placeAddress == "Дворцовая площадь, 2" || placeAddress == "Palace Square, 2")

    }

    @Test
    fun savePlace() {
        mapsListScreen
            .chooseMapByIndex(0)
            .zoomIn()
            .clickOnMap()
            .clickSavePlace()
            .clickOnMap()
            .getPlaceMenu().check(
                ViewAssertions.matches(
                    AllOf.allOf(
                        ViewMatchers.isDisplayed(),
                    )
                )
            )
    }

    @Test
    fun openMap() {
        mapsListScreen
            .chooseMapByIndex(0)
            .zoomIn()
            .clickOnMap()
            .getPlaceMenu().check(
                ViewAssertions.matches(
                    AllOf.allOf(
                        ViewMatchers.isDisplayed(),
                    )
                )
            )
    }
}