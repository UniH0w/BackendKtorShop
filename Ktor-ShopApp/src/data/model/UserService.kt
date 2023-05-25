package com.example.data.model

import ch.qos.logback.core.subst.Token

data class UserService(
    val firstName: String,
    val lastName: String,
    val email:String,
    val token: String
)

