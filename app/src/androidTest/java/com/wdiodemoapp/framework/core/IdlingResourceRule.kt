package com.wdiodemoapp.framework.core

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import org.junit.rules.ExternalResource

/**
 * Registers Espresso IdlingResources for a test class and unregisters them cleanly afterward.
 */
class IdlingResourceRule(
    private vararg val resources: IdlingResource,
) : ExternalResource() {

    override fun before() {
        IdlingRegistry.getInstance().register(*resources)
    }

    override fun after() {
        IdlingRegistry.getInstance().unregister(*resources)
    }
}
