package com.d2d.challenge.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.d2d.challenge.R
import com.d2d.challenge.core.di.module.ServiceModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@UninstallModules(ServiceModule::class)
@HiltAndroidTest
class MapsActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)


    @get:Rule(order = 1)
    val activityTestRule = ActivityTestRule(MapsActivity::class.java)


    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testDisplay() {
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withId(R.id.textview_source)).check(matches(isDisplayed()))
        onView(withId(R.id.textview_destination)).check(matches(isDisplayed()))
        onView(withId(R.id.textview_ride_status)).check(matches(isDisplayed()))
    }

    @Test
    fun testSourceAddressTextView() {
        delay(2000)
        onView(withId(R.id.textview_source)).perform(Matchers.setTextInTextView("Passau"));
        delay(2000)
    }

    @Test
    fun testDestinationAddressTextView() {
        delay(2000)
        onView(withId(R.id.textview_destination)).perform(Matchers.setTextInTextView("Berlin"));
        delay(2000)
    }

    @Test
    fun testRideStatusTextView() {
        delay(2000)
        onView(withId(R.id.textview_ride_status)).perform(Matchers.setTextInTextView("In Vehicle"));
        delay(2000)
    }





    private fun delay(i: Long) = Thread.sleep(i)

}