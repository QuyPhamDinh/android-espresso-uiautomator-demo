package com.wdiodemoapp.testshared

/**
 * Driver-agnostic navigation screen contract shared by Espresso and UIAutomator Robots.
 */
interface NavigationScreen {
    fun openHome(): NavigationScreen
    fun openWebView(): NavigationScreen
    fun openLogin(): NavigationScreen
    fun openForms(): NavigationScreen
    fun openSwipe(): NavigationScreen
    fun openDrag(): NavigationScreen
    fun openTab(tabContentDesc: String): NavigationScreen
    fun assertTabBarReady(): NavigationScreen
    fun assertTabSelected(tabContentDesc: String): NavigationScreen
}

/**
 * Driver-agnostic Forms screen contract shared by Espresso and UIAutomator Robots.
 */
interface FormsScreen {
    fun open(): FormsScreen
    fun assertVisible(): FormsScreen
    fun enterText(text: String): FormsScreen
    fun assertInputText(expected: String): FormsScreen
    fun assertInputEmpty(): FormsScreen
    fun setSwitch(on: Boolean): FormsScreen
    fun assertSwitchChecked(expected: Boolean): FormsScreen
    fun assertStateCleared(): FormsScreen
}
