package com.example.data.table

import org.jetbrains.exposed.sql.Table


object UserTable:Table() {

    val email = varchar("email",512)
    val firstName = varchar("firstName",512)
    val lastName = varchar("lastName",512)
    val  password = varchar("password",512)
    val phoneNumber = varchar("phoneNumber",512)

    override val primaryKey: PrimaryKey = PrimaryKey(email)
}