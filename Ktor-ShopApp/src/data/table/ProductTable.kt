package com.example.data.table

import org.jetbrains.exposed.sql.Table

object ProductTable: Table(){

    val id  = varchar("id",512)
    val manufacturer = text("manufacturer")
    val model = text("model")
    val description =text("description")
    val price = text("price")
    val image = text("image")
    val cartid = text("cartid")
    val favoriteid = text("favoriteid")

    override val  primaryKey: PrimaryKey = PrimaryKey(id)

}