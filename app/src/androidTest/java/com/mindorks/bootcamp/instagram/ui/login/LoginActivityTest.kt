package com.mindorks.bootcamp.instagram.ui.login

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.TestComponentRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

class LoginActivityTest {

    private val component =
        TestComponentRule(InstrumentationRegistry.getInstrumentation().targetContext)

    @get:Rule
    val chain = RuleChain.outerRule(component)

    @Before
    fun setup() {

    }

    @Test
    fun testCheckViewsDisplay() {
        launch(LoginActivity::class.java)
        onView(withId(R.id.layout_email))
            .check(matches(isDisplayed()))
        onView(withId(R.id.layout_password))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bt_login))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenValidEmailAndValidPwd_whenLogin_shouldOpenMainActivity() {
        launch(LoginActivity::class.java)

        onView(withId(R.id.et_email)).perform(
            typeText("test@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.et_password)).perform(
            typeText("password"),
            closeSoftKeyboard()
        )

        onView(withId(R.id.bt_login)).perform(click())

        onView(withId(R.id.bottomNavigation))
            .check(matches(isDisplayed()))
    }

}