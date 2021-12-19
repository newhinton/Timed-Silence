package de.felixnuesse.timedsilence

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Rule
import org.junit.ClassRule
import org.junit.runners.JUnit4

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule





/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(JUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    public var rule: ActivityScenarioRule<*> = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testTakeScreenshot() {
        Screengrab.screenshot("before_button_click")

        // Your custom onView...
        onView(withId(R.id.fab)).perform(click())
        Screengrab.screenshot("after_button_click")
    }

    companion object {
        @ClassRule @JvmField
        val localeTestRule: LocaleTestRule = LocaleTestRule()
    }
}
