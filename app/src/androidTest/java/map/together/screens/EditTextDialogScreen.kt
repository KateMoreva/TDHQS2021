package map.together.screens

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import map.together.R

class EditTextDialogScreen<T>(private val parentScreen: T) {

    fun enterValue(value: String): EditTextDialogScreen<T> {
        Espresso.onView(ViewMatchers.withId(R.id.md_input_message))
                .perform(
                        ViewActions.clearText(),
                        ViewActions.typeTextIntoFocusedView(value),
                        ViewActions.closeSoftKeyboard()
                )
        return this
    }

    fun pressPositiveButton(): T {
        Espresso.onView(ViewMatchers.withId(R.id.md_button_positive))
                .perform(ViewActions.click())
        return parentScreen
    }

    fun pressNegativeButton(): T {
        Espresso.onView(ViewMatchers.withId(R.id.md_button_negative))
                .perform(ViewActions.click())
        return parentScreen
    }

    fun pressNeutralButton(): T {
        Espresso.onView(ViewMatchers.withId(R.id.md_button_neutral))
                .perform(ViewActions.click())
        return parentScreen
    }


}