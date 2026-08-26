package com.example.creditcardcontroller.ui.composables.categories

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

fun iconoDeCategoria(icono: String): ImageVector = when (icono) {
    "Restaurant" -> Icons.Default.Restaurant
    "ShoppingBag" -> Icons.Default.ShoppingBag
    "DirectionsCar" -> Icons.Default.DirectionsCar
    "Movie" -> Icons.Default.Movie
    "AddChart" -> Icons.Default.AddChart
    "CreditCard" -> Icons.Default.CreditCard
    "AccountBalanceWallet" -> Icons.Default.AccountBalanceWallet
    "AccountBalance" -> Icons.Default.AccountBalance
    "Payments" -> Icons.Default.Payments
    "ShoppingCart" -> Icons.Default.ShoppingCart
    else -> Icons.Default.MoreHoriz
}

fun colorDeCategoria(hex: String): Color {
    val clean = hex.removePrefix("#")
    val rgb = clean.toLongOrNull(16)
    return if (clean.length == 6 && rgb != null) {
        Color(0xFF000000L or rgb)
    } else {
        Color(0xFF757575)
    }
}
