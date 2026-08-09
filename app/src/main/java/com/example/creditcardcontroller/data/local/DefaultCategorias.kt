package com.example.creditcardcontroller.data.local

import com.example.creditcardcontroller.data.local.entities.CategoriaEntity

object DefaultCategorias {
    val lista = listOf(
        CategoriaEntity(nombre = "Comida", icono = "Restaurant", color = "#F4511E"),
        CategoriaEntity(nombre = "Compras", icono = "ShoppingBag", color = "#3949AB"),
        CategoriaEntity(nombre = "Transporte", icono = "DirectionsCar", color = "#43A047"),
        CategoriaEntity(nombre = "Ocio", icono = "Movie", color = "#8E24AA"),
        CategoriaEntity(nombre = "Otros", icono = "MoreHoriz", color = "#757575")
    )
}
