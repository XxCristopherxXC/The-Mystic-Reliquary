package com.example.tmrcatalog.util

import android.content.Context
import android.content.SharedPreferences

class ActivationManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tmr_activation_prefs", Context.MODE_PRIVATE)

    private val MASTER_INSTALL_KEY = "Mimundo18@"
    private val MAX_ATTEMPTS = 3

    fun isAppActivated(): Boolean {
        return prefs.getBoolean("is_activated", false)
    }

    fun isAppBlocked(): Boolean {
        return prefs.getBoolean("is_blocked", false)
    }

    fun getRemainingAttempts(): Int {
        val usedAttempts = prefs.getInt("failed_attempts", 0)
        return (MAX_ATTEMPTS - usedAttempts).coerceAtLeast(0)
    }

    fun activateApp(inputKey: String): Boolean {
        if (isAppBlocked()) return false

        if (inputKey.trim() == MASTER_INSTALL_KEY) {
            prefs.edit()
                .putBoolean("is_activated", true)
                .putInt("failed_attempts", 0)
                .apply()
            return true
        } else {
            val currentAttempts = prefs.getInt("failed_attempts", 0) + 1
            val editor = prefs.edit().putInt("failed_attempts", currentAttempts)

            if (currentAttempts >= MAX_ATTEMPTS) {
                editor.putBoolean("is_blocked", true)
            }
            editor.apply()
            return false
        }
    }
}