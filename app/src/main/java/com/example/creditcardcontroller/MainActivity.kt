package com.example.creditcardcontroller

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.fragment.app.FragmentActivity
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.AppTheme
import com.example.creditcardcontroller.data.local.SettingsDataStore
import com.example.creditcardcontroller.ui.scaffold.MainScaffold
import com.example.creditcardcontroller.ui.screens.login.LoginScreen
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsDataStore = remember { SettingsDataStore(applicationContext) }

            LaunchedEffect(Unit) {
                AppDatabase.getDatabase(applicationContext).seedCategoriasSiVacia()
                AppDatabase.getDatabase(applicationContext).seedTarjetaCuentaSiVacia()
            }

            val theme by settingsDataStore.themeFlow.collectAsState(initial = AppTheme.SYSTEM)
            val biometricEnabled by settingsDataStore.biometricEnabledFlow.collectAsState(initial = null)
            val autoLockEnabled by settingsDataStore.autoLockEnabledFlow.collectAsState(initial = false)
            
            var isAuthenticated by remember { mutableStateOf(false) }
            
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START) {
                        if (biometricEnabled == true && autoLockEnabled) {
                            isAuthenticated = false
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            // Initial state and when biometric is disabled
            LaunchedEffect(biometricEnabled) {
                if (biometricEnabled == false) {
                    isAuthenticated = true
                }
            }

            if (biometricEnabled == null) return@setContent

            val darkTheme = when (theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            CreditCardControllerTheme(darkTheme = darkTheme) {
                if (biometricEnabled == true && !isAuthenticated) {
                    LoginScreen(onAuthenticated = { isAuthenticated = true })
                } else {
                    MainScaffold()
                }
            }
        }
    }
}
