package com.d2d.challenge.view

import android.view.View
import android.widget.TextView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher


/**
 * Singleton Matchers object to perform automated tests within a view.
 */
object Matchers {

    /**
     * This implements ViewAction interface which is responsible for perform action within a given view. View perform
     * action based on given parameter.
     * @param value string value which is eligible for view action
     * @return returns an instance of ViewAction object
     */
    fun setTextInTextView(value: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return CoreMatchers.allOf(ViewMatchers.isDisplayed(), ViewMatchers.isAssignableFrom(
                    TextView::class.java))
            }

            override fun perform(uiController: UiController, view: View) {
                (view as TextView).text = value
            }

            override fun getDescription(): String {
                return ""
            }
        }
    }
}