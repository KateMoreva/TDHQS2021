package map.together.screens

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.yandex.mapkit.transport.masstransit.Wait
import map.together.R
import map.together.utils.RecyclerViewItemCountAssertion
import map.together.utils.WaitForAction
import map.together.utils.WithViewInsideHolder
import map.together.utils.withViewAtPosition
import map.together.viewholders.LayerViewHolder
import map.together.viewholders.MapViewHolder
import org.hamcrest.Matcher


class MainScreen {
    
    private val WAITING_TIMEOUT = 3000L;
    
    fun zoomIn(): MainScreen {
        for (i in (0..8)) {
            onView(ViewMatchers.isRoot())
                .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
            onView(ViewMatchers.withId(R.id.zoom_in_id))
                .perform(ViewActions.click())
        }
        return this
    }

    fun clickOnMap(): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.mapview))
            .perform(ViewActions.click())
        return this
    }

    fun getPlaceMenu(): ViewInteraction {
        Espresso.onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(2000L))
        return Espresso.onView(ViewMatchers.withId(R.id.category_on_tap_adress_id))
    }

    fun getAddress(): String {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        return getText(ViewMatchers.withId(R.id.category_on_tap_adress_id)).toString()
    }

    fun getCategoryName(): String {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        return getText(ViewMatchers.withId(R.id.category_on_tap_name_id)).toString()
    }

    fun getUserPlaceName(): String {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        return getText(ViewMatchers.withId(R.id.category_on_tap_place_name_id)).toString()
    }


    fun typeUserPlaceName(name: String): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.category_on_tap_place_name_id))
            .perform(ViewActions.replaceText(name))
        return this
    }


    fun getUserAdditionalPlaceData(): String {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        return getText(ViewMatchers.withId(R.id.category_on_tap_place_description_id)).toString()
    }

    fun getUserName(): String {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        return getText(ViewMatchers.withId(R.id.category_on_tap_user_name_id)).toString()
    }

    fun clickSavePlace(): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.category_on_tap_save_changes_id))
            .perform(ViewActions.click())
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        return this
    }

    fun getSavePlaceButton(): ViewInteraction {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        return onView(ViewMatchers.withId(R.id.category_on_tap_save_changes_id))
    }

    fun clickDrawing(): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.line_point)).perform(ViewActions.click())
        return this
    }

    fun openLayers(): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.layers_btn))
            .perform(ViewActions.click())
        return this
    }

    fun activateSearch(): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.search_button_id))
            .perform(ViewActions.click())
        return this
    }

    fun clearSearch(): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.search_text_clear))
            .perform(ViewActions.click())
        return this
    }

    fun typeSearchRequest(search: String): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.search_text_field))
            .perform(
                ViewActions.replaceText(search),
                ViewActions.clearText(),
                ViewActions.typeText(search),
                ViewActions.closeSoftKeyboard(),
            )
        return this
    }

    fun selectSearchResult(index: Int): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.search_res_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<MapViewHolder>(
                    index,
                    ViewActions.click()
                )
            )
        return this
    }

    fun openUsers(): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.open_users))
            .perform(ViewActions.click())
        return this
    }

    fun createLayer(): MainScreen {
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.resizable_layers_menu))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.add_layer_btn))
            .perform(ViewActions.click())
        return this
    }

    fun checkLayerCreated(index: Int): MainScreen {
        onView(ViewMatchers.withId(R.id.layers_list))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.layers_list))
            .check(matches(withViewAtPosition(index, isDisplayed())))
        return this
    }

    fun checkUserPresented(index: Int): MainScreen {
        onView(ViewMatchers.withId(R.id.users_list))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(WAITING_TIMEOUT))
        onView(ViewMatchers.withId(R.id.users_list))
            .check(matches(withViewAtPosition(index, isDisplayed())))
        return this
    }

    fun getText(matcher: Matcher<View?>?): String? {
        val stringHolder = arrayOf<String?>(null)
        onView(matcher).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "getting text from a TextView"
            }

            override fun perform(uiController: UiController, view: View) {
                val tv = view as TextView //Save, because of check in getConstraints()
                stringHolder[0] = tv.text.toString()
            }
        })
        return stringHolder[0]
    }

    fun removeLayer(index: Int): MainScreen {
        onView(ViewMatchers.withId(R.id.layers_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<LayerViewHolder>(
                    index,
                    WithViewInsideHolder.clickChildViewWithId(R.id.remove_layer)
                )
            )
        onView(ViewMatchers.isRoot())
            .perform(WaitForAction.waitFor(2000L))
        return this
    }

    fun searchListIsEmpty(): MainScreen {
        //теневой элемент,
        onView(ViewMatchers.withId(R.id.layers_list))
            .check(RecyclerViewItemCountAssertion(1));
        return this
    }

    fun isLayersListEmpty(): MainScreen {
        onView(ViewMatchers.withId(R.id.layers_list))
            .check(RecyclerViewItemCountAssertion(0));
        return this
    }
}