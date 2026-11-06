package com.example.tmrcatalog.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("tmr_users_prefs", Context.MODE_PRIVATE)

    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        // Usuario por defecto si no existe ninguno guardado aún
        if (!prefs.contains("user_admin")) {
            prefs.edit().putString("user_admin", "1234").apply()
        }
    }

    fun login(user: String, pass: String): Boolean {
        val cleanUser = user.trim()
        val cleanPass = pass.trim()

        if (cleanUser.isEmpty() || cleanPass.isEmpty()) return false

        val storedPass = prefs.getString("user_$cleanUser", null)

        return if (storedPass != null && storedPass == cleanPass) {
            prefs.edit().putBoolean("is_logged_in", true).apply()
            _isLoggedIn.value = true
            true
        } else {
            false
        }
    }

    fun register(user: String, pass: String): Boolean {
        val cleanUser = user.trim()
        val cleanPass = pass.trim()

        if (cleanUser.isEmpty() || cleanPass.isEmpty()) return false

        // Verificar si el usuario ya existe guardado
        if (prefs.contains("user_$cleanUser")) return false

        // Guardar nuevo usuario de forma permanente en la memoria interna
        prefs.edit().putString("user_$cleanUser", cleanPass).apply()
        return true
    }

    fun logout() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
        _isLoggedIn.value = false
    }
}