package com.paisapulse.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector

import org.hamcrest.Matchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testFullAppFlow() {
        // 1. Handle the SMS Permission Dialog if it appears
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val allowButton = device.findObject(UiSelector().textMatches("(?i)ALLOW|WHILE USING THE APP"))
        if (allowButton.exists()) { allowButton.click() }

        // 2. Wait for the UI to settle
        Thread.sleep(2000)

        // 3. Test Search (Ensure keyboard closes so it doesn't hide chips)
        onView(withId(R.id.etSearch))
            .perform(typeText("Test"), closeSoftKeyboard())

        // 4. Test Chips (The logic you verified in Day 19)
        onView(withId(R.id.chipFood)).perform(click())
        onView(withId(R.id.chipBills)).perform(click())

        // 5. Verify the Summary Card is still showing correct data
        onView(withId(R.id.tvTotalAmount))
            .check(matches(withText(containsString("₹"))))
    }
}