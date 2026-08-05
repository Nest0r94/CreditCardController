package com.example.creditcardcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.creditcardcontroller.data.local.AppTheme
import com.example.creditcardcontroller.data.local.SettingsDataStore
import com.example.creditcardcontroller.ui.scaffold.MainScaffold
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsDataStore = remember { SettingsDataStore(applicationContext) }
            val theme by settingsDataStore.themeFlow.collectAsState(initial = AppTheme.SYSTEM)

            val darkTheme = when (theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            CreditCardControllerTheme(darkTheme = darkTheme) {
                MainScaffold()
            }
        }
    }
}
