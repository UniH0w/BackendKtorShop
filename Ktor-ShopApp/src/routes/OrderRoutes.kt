package com.example.routes

import com.example.data.model.Order
import com.example.data.model.SimpleResponse
import com.example.repository.Repo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val ORDER = "$API_VERSION/orders"
@Location(ORDER)
class OrderCreate

fun Route.OrderRoutes(
    db: Repo,
    hashFunction:(String) -> String
) {
    authenticate("jwt") {

        post<OrderCreate> {
            val order =try {
                call.receive<Order>()
            }catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }
            try {
                db.addOrder(order)
                call.respond(HttpStatusCode.OK,SimpleResponse(true, "Order Added"))
            }catch (e: Exception){
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem"))
            }
        }
    }

}