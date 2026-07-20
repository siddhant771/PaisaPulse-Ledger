package com.paisapulse.app.security

import android.content.Context

object BiometricPreferences {
    private const val FILE = "security_preferences"
    private const val KEY_ENABLED = "biometric_unlock_enabled"

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ENABLED, enabled)
            .apply()
    }
}
