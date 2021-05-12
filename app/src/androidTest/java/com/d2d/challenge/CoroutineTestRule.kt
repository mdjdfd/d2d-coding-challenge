package com.d2d.challenge

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Customized test rule class which implements TestRule interface. Class is responsible for testing suspend function
 * launched inside coroutine scope.
 */

@ExperimentalCoroutinesApi
class CoroutineTestRule : TestRule {

    //Any task scheduled to be executed without delay executed immediately
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    //Provides more control over execution within coroutine tests
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    /**
     * Apply and set the coroutine scope as well as dispatcher
     */
    override fun apply(base: Statement, description: Description?) = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(testCoroutineDispatcher)

            base.evaluate()

            Dispatchers.resetMain()
            testCoroutineScope.cleanupTestCoroutines()
        }
    }

    /**
     * run blocking test for suspend functions
     */
    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
        testCoroutineScope.runBlockingTest { block() }

}