package com.example.routes

import com.example.data.model.Product
import com.example.data.model.SimpleResponse
import com.example.data.model.User
import com.example.data.table.ProductTable
import com.example.data.table.UserTable
import com.example.repository.Repo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PRODUCT = "$API_VERSION/products"
const val CREATE_PRODUCT = "$PRODUCT/create"
const val DELETE_PRODUCT = "$PRODUCT/delete"
const val GET_ID_PRODUCT = "$PRODUCT/id"

@Location(CREATE_PRODUCT)
class ProductCreateRoute

@Location(DELETE_PRODUCT)
class ProductDeleteRoute

@Location(PRODUCT)
class ProductGetRoute

@Location(GET_ID_PRODUCT)
class ProductGetIdRoute

fun Route.ProductRoutes(
    db:Repo,
    hashFunction:(String) -> String
){
    authenticate("jwt") {

        post<ProductCreateRoute> {

            val product = try {
                call.receive<Product>()
            }catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"Missing Fields"))
                return@post
            }

            try {
                db.addProduct(product)
                call.respond(HttpStatusCode.OK,SimpleResponse(true,"Product Added"))
            }catch (e:Exception){
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message?:"Some Problem"))
            }
        }

        get<ProductGetRoute> {
            val manufacturer = try {
                call.request.queryParameters["manufacturer"]!!
            }catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"QueryParameter:id is not present"))
                return@get
            }
            try {
                val product = db.getAllProduct(manufacturer)
                call.respond(HttpStatusCode.OK,product)
            }catch (e:Exception){
                call.respond(HttpStatusCode.Conflict, emptyList<Product>())
            }
        }

        get<ProductGetIdRoute>  {
            val productId = try {
                call.request.queryParameters["id"]!!
            } catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"QueryParameter:id is not present"))
                return@get
            }

            try {

                val product = db.getIdProduct(productId)
                call.respond(HttpStatusCode.OK,product)
            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message ?: "Some problem Occurred!"))
            }

        }


        delete<ProductDeleteRoute> {

            val productId = try{
                call.request.queryParameters["id"]!!
            }catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"QueryParameter:id is not present"))
                return@delete
            }
            try {
                db.deleteProduct(productId)
                call.respond(HttpStatusCode.OK,SimpleResponse(true,"Note Deleted Successfully!"))

            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message ?: "Some problem Occurred!"))
            }
        }
    }
}