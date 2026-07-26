package com.example.creditcardcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.creditcardcontroller.ui.scaffold.MainScaffold
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CreditCardControllerTheme {
                MainScaffold()
            }
        }
    }
}
