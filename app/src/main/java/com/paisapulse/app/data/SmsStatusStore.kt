package com.paisapulse.app.data

import android.content.Context
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

object SmsStatusStore {
    private const val FILE = "sms_status"
    private const val KEY = "message"
    fun set(context: Context, message: String) { context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString(KEY, message).apply() }
    fun get(context: Context): String? = context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY, null)

    fun messages(context: Context): Flow<String?> = callbackFlow {
        val preferences = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY) trySend(preferences.getString(KEY, null))
        }
        trySend(preferences.getString(KEY, null))
        preferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()
}
