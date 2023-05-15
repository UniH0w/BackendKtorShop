package com.example.data.model

import io.ktor.auth.*

data class User(
    val email:String,
    val password:String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
):Principal
