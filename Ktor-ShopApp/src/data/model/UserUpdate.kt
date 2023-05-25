package com.example.data.model

import com.example.routes.UserUpdatePassword

data class UserUpdate(
    val email:String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
)
