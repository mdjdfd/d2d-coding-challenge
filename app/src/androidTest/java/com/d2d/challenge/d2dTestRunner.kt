package com.d2d.challenge

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication


/**
 * Custom JUnit test runner for Dagger Hilt test application which can be used for android instrumentation
 * or Robolectric tests using Hilt. Needs to configure in app build.gradle file
 */
class d2dTestRunner : AndroidJUnitRunner() {

    /**
     * Perform instantiation of the new Application class.
     * @param cl ClassLoader where the object is instantiate
     * @param className name of the class which implementing Application object
     * @param context context to initialize the Application object
     * @return returns newly created Application object
     */
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}