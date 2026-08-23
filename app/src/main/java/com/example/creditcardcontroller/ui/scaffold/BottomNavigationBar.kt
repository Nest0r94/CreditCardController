package com.example.creditcardcontroller.ui.scaffold

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*

class BottomBarShape(private val notchRadius: Dp, private val notchDepth: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radiusPx = with(density) { notchRadius.toPx() }
        val depthPx = with(density) { notchDepth.toPx() }
        val path = Path().apply {
            val notchCenter = size.width / 2
            
            moveTo(0f, 0f)
            // Tramo recto izquierda
            lineTo(notchCenter - radiusPx * 1.5f, 0f)
            
            // Curva suave hacia el fondo del notch
            cubicTo(
                notchCenter - radiusPx * 0.8f, 0f,
                notchCenter - radiusPx * 0.8f, depthPx,
                notchCenter, depthPx
            )
            
            // Curva suave hacia la superficie derecha
            cubicTo(
                notchCenter + radiusPx * 0.8f, depthPx,
                notchCenter + radiusPx * 0.8f, 0f,
                notchCenter + radiusPx * 1.5f, 0f
            )
            
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun BottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit) {
    val items = listOf(
        NavigationItem("Presupuesto", Icons.Default.AccountBalance, "presupuesto"),
        NavigationItem("Balances", Icons.Default.Home, "balances"),
        NavigationItem("Datos", Icons.Default.BarChart, "estadisticas"),
        NavigationItem("Nuevo", Icons.Default.Add, "nuevo"),
        NavigationItem("Tarjetas", Icons.Default.CreditCard, "tarjetas"),
        NavigationItem("Promos", Icons.Default.LocalOffer, "promos"),
        NavigationItem("Ajustes", Icons.Default.Settings, "ajustes"),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier
                .height(85.dp)
                .graphicsLayer {
                    shape = BottomBarShape(notchRadius = 36.dp, notchDepth = 20.dp)
                    clip = true
                },
            tonalElevation = 8.dp
        ) {
            items.forEachIndexed { index, item ->
                if (index == 3) {
                    // Espacio central
                    NavigationBarItem(
                        selected = false,
                        onClick = { onNavigate(item.route) },
                        icon = { Spacer(Modifier.size(52.dp)) },
                        label = { Text("") },
                        enabled = true
                    )
                } else {
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = item.icon, 
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            ) 
                        },
                        label = { 
                            Text(
                                text = item.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp
                            )
                        },
                        selected = currentRoute == item.route,
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }

        // Botón central con ajustes proporcionales a la nueva altura
        val middleItem = items[3]
        FloatingActionButton(
            onClick = { onNavigate(middleItem.route) },
            shape = CircleShape,
            containerColor = if (currentRoute == middleItem.route) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-10).dp)
                .size(52.dp),
            elevation = FloatingActionButtonDefaults.elevation(6.dp)
        ) {
            Icon(
                imageVector = middleItem.icon,
                contentDescription = middleItem.label,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
