package com.example.tmrcatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.tmrcatalog.ui.theme.TMRCatalogTheme
import com.example.tmrcatalog.util.ActivationManager
import com.example.tmrcatalog.viewmodel.AuthViewModel
import com.example.tmrcatalog.viewmodel.CatalogViewModel

enum class Screen {
    ACTIVATION, LOGIN, REGISTER, HOME, CATALOG, TABLE
}

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val catalogViewModel: CatalogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activationManager = ActivationManager(this)

        setContent {
            TMRCatalogTheme {
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

                // Determinar la pantalla inicial basada en activación e inicio de sesión guardados
                var currentScreen by rememberSaveable {
                    mutableStateOf(
                        when {
                            !activationManager.isAppActivated() -> Screen.ACTIVATION
                            isLoggedIn -> Screen.HOME
                            else -> Screen.LOGIN
                        }
                    )
                }

                when (currentScreen) {
                    Screen.ACTIVATION -> ActivationScreen(
                        onActivationSuccess = { currentScreen = Screen.LOGIN }
                    )
                    Screen.LOGIN -> LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = { currentScreen = Screen.HOME },
                        onNavigateToRegister = { currentScreen = Screen.REGISTER }
                    )
                    Screen.REGISTER -> RegisterScreen(
                        authViewModel = authViewModel,
                        onRegisterSuccess = { currentScreen = Screen.LOGIN },
                        onBackToLogin = { currentScreen = Screen.LOGIN }
                    )
                    Screen.HOME -> HomeScreen(
                        onNavigateToCatalog = { currentScreen = Screen.CATALOG },
                        onNavigateToTable = { currentScreen = Screen.TABLE },
                        onLogout = {
                            authViewModel.logout()
                            currentScreen = Screen.LOGIN
                        }
                    )
                    Screen.CATALOG -> CatalogScreen(
                        viewModel = catalogViewModel,
                        onBackToHome = { currentScreen = Screen.HOME }
                    )
                    Screen.TABLE -> HistoryTableScreen(
                        viewModel = catalogViewModel,
                        onBackToHome = { currentScreen = Screen.HOME }
                    )
                }
            }
        }
    }
}