package map.together

import android.view.View
import android.widget.Checkable
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.Description
import org.hamcrest.Matcher


fun setChecked(checked: Boolean): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): BaseMatcher<View?> {
            return object : BaseMatcher<View?>() {
                override fun matches(item: Any): Boolean {
                    return isA(Checkable::class.java).matches(item)
                }

                override fun describeMismatch(item: Any?, mismatchDescription: Description?) {}
                override fun describeTo(description: Description?) {}
            }
        }

        override fun getDescription(): String {
            return "Set checked-state to the Checkable element"
        }

        override fun perform(uiController: UiController?, view: View) {
            val checkableView: Checkable = view as Checkable
            checkableView.isChecked = checked
        }
    }
}

fun getText(matcher: Matcher<View?>?): String? {
    val stringHolder = arrayOf<String?>(null)
    onView(matcher).perform(object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isAssignableFrom(TextView::class.java)
        }

        override fun getDescription(): String {
            return "Getting text from a TextView"
        }

        override fun perform(uiController: UiController, view: View) {
            val tv = view as TextView
            stringHolder[0] = tv.text.toString()
        }
    })
    return stringHolder[0]
}

fun isChecked(matcher: Matcher<View?>?): Boolean? {
    val valueHolder = arrayOf<Boolean?>(null)
    onView(matcher).perform(object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isAssignableFrom(TextView::class.java)
        }

        override fun getDescription(): String {
            return "Getting checked-state from an Checkable element"
        }

        override fun perform(uiController: UiController, view: View) {
            val checkbox = view as Checkable
            valueHolder[0] = checkbox.isChecked
        }
    })
    return valueHolder[0]
}
