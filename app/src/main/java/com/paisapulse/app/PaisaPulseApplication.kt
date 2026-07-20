package com.paisapulse.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PaisaPulseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase initializes automatically here
    }
}
