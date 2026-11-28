package com.wdiodemoapp.testshared

import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

/**
 * Out-of-process UIAutomator implementation of [NavigationScreen] for black-box test APKs.
 */
open class UiAutomatorNavigationRobot(
    private val device: UiDevice,
    private val packageName: String = TestConfig.packageName,
) : NavigationScreen {

    override fun openHome(): UiAutomatorNavigationRobot = openTab(AccessibilityIds.Tabs.HOME)
    override fun openWebView(): UiAutomatorNavigationRobot = openTab(AccessibilityIds.Tabs.WEBVIEW)
    override fun openLogin(): UiAutomatorNavigationRobot = openTab(AccessibilityIds.Tabs.LOGIN)
    override fun openForms(): UiAutomatorNavigationRobot = openTab(AccessibilityIds.Tabs.FORMS)
    override fun openSwipe(): UiAutomatorNavigationRobot = openTab(AccessibilityIds.Tabs.SWIPE)
    override fun openDrag(): UiAutomatorNavigationRobot = openTab(AccessibilityIds.Tabs.DRAG)

    override fun openTab(tabContentDesc: String): UiAutomatorNavigationRobot = apply {
        waitFor(device, packageName, tabContentDesc).click()
    }

    override fun assertTabBarReady(): UiAutomatorNavigationRobot = apply {
        waitFor(device, packageName, AccessibilityIds.Tabs.HOME)
    }

    override fun assertTabSelected(tabContentDesc: String): UiAutomatorNavigationRobot = apply {
        assertTrue(
            "Expected tab '$tabContentDesc' to be selected",
            device.wait(
                Until.findObject(descSelector(packageName, tabContentDesc).selected(true)),
                TestConfig.uiTimeoutMs,
            ) != null,
        )
    }
}

/**
 * Out-of-process UIAutomator implementation of [FormsScreen] for black-box test APKs.
 */
open class UiAutomatorFormsRobot(
    private val device: UiDevice,
    private val navigation: NavigationScreen = UiAutomatorNavigationRobot(device),
    private val packageName: String = TestConfig.packageName,
) : FormsScreen {

    override fun open(): UiAutomatorFormsRobot = apply {
        navigation.openForms()
        assertVisible()
    }

    override fun assertVisible(): UiAutomatorFormsRobot = apply {
        waitFor(device, packageName, AccessibilityIds.Screens.FORMS)
    }

    override fun enterText(text: String): UiAutomatorFormsRobot = apply {
        val input = waitFor(device, packageName, AccessibilityIds.Forms.TEXT_INPUT)
        input.text = text
    }

    override fun assertInputText(expected: String): UiAutomatorFormsRobot = apply {
        val actual = waitFor(device, packageName, AccessibilityIds.Forms.TEXT_INPUT).text.orEmpty()
        assertEquals("Unexpected Forms input text", expected, actual)
    }

    override fun assertInputEmpty(): UiAutomatorFormsRobot = apply {
        val inputText = waitFor(device, packageName, AccessibilityIds.Forms.TEXT_INPUT).text.orEmpty()
        // UIAutomator exposes an empty EditText's hint string as its text property.
        assertTrue(
            "Expected cleared/hint text in Forms input, found '$inputText'",
            inputText.isEmpty() || inputText == UiText.Forms.INPUT_HINT,
        )
    }

    override fun setSwitch(on: Boolean): UiAutomatorFormsRobot = apply {
        val switchToggle = waitFor(device, packageName, AccessibilityIds.Forms.SWITCH)
        if (switchToggle.isChecked != on) {
            switchToggle.click()
        }
        assertSwitchChecked(on)
    }

    override fun assertSwitchChecked(expected: Boolean): UiAutomatorFormsRobot = apply {
        assertTrue(
            "Expected Forms switch checked=$expected",
            device.wait(
                Until.findObject(descSelector(packageName, AccessibilityIds.Forms.SWITCH).checked(expected)),
                TestConfig.uiTimeoutMs,
            ) != null,
        )
    }

    override fun assertStateCleared(): UiAutomatorFormsRobot = apply {
        assertInputEmpty()
        assertSwitchChecked(false)
    }
}

private fun waitFor(device: UiDevice, packageName: String, contentDesc: String): UiObject2 =
    checkNotNull(device.wait(Until.findObject(descSelector(packageName, contentDesc)), TestConfig.uiTimeoutMs)) {
        "Element with content-desc='$contentDesc' did not appear in $packageName"
    }

private fun descSelector(packageName: String, contentDesc: String): BySelector =
    By.desc(contentDesc).pkg(packageName)
