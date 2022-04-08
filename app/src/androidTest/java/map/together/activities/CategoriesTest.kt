package map.together.activities


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import map.together.R
import map.together.activities.auth.LoginActivity
import map.together.screens.LoginScreen
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf
import org.junit.Rule
import org.junit.Test
import java.util.*

@LargeTest
class CategoriesTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(LoginActivity::class.java)
    private val loginScreen = LoginScreen()

    @Test
    fun categoriesChangeNameTest() {

        val newCategoryName = UUID.randomUUID().toString()
        loginScreen
            .pressConfirmButton()
            .pressSettingsButton()
            .pressCategoriesButton()
            .pressChangeCategoryButton()
            .enterValueCategory(newCategoryName)
            .pressPositiveButtonCategory()
            .isCategoryNameEqualsValue(newCategoryName)

    }

    @Test
    fun categoriesAddTest() {

        val newCategoryName = UUID.randomUUID().toString()
        loginScreen
            .pressConfirmButton()
            .pressSettingsButton()
            .pressCategoriesButton()
            .pressAddCategoryButton()
            .enterValueCategory(newCategoryName)
            .pressPositiveButtonCategory()
            .isCategoryNameEqualsValue(newCategoryName)

    }

    @Test
    fun categoriesGetTest() {

        loginScreen
            .pressConfirmButton()
            .pressSettingsButton()
            .pressCategoriesButton()
            .getList().check(
                matches(
                    AllOf.allOf(
                        isDisplayed()
                    )
                )
            )

    }

}
