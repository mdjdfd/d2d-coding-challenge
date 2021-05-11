package com.d2d.challenge.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


/**
 * Application starting point.
 * HiltAndroidApp annotation indicate application class where dagger components will be generated
 */
@HiltAndroidApp
class App: Application()