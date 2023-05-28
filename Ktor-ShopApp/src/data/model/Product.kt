package com.example.data.model

import com.example.data.table.ProductTable


data class Product(
    val id: String,
    val manufacturer: String,
    val model: String,
    val description: String,
    val image: String,
    val price: String,
    val cartid :String,
    val favoriteid: String
)
