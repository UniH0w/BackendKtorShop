package com.example.routes

import com.example.data.model.*
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
const val POST_BASKET_PRODUCT = "$PRODUCT/create/basket"
const val GET_BASKET_PRODUCT = "$PRODUCT/basket"
const val REMOVE_BASKET_PRODUCT ="$PRODUCT/basket/remove"
const val POST_FAVORITE_PRODUCT = "$PRODUCT/create/favorite"
const val GET_FAVORITE_PRODUCT = "$PRODUCT/favorite"
const val REMOVE_FAVORITE_PRODUCT ="$PRODUCT/remove/favorite"

@Location(POST_FAVORITE_PRODUCT)
class ProductCreateFavorite
@Location(GET_FAVORITE_PRODUCT)
class ProductGetFavorite
@Location(REMOVE_FAVORITE_PRODUCT)
class ProductRemoveFavorite

@Location(CREATE_PRODUCT)
class ProductCreateRoute
@Location(POST_BASKET_PRODUCT)
class ProductCreateBasket
@Location(GET_BASKET_PRODUCT)
class ProductGetBasket
@Location(REMOVE_BASKET_PRODUCT)
class ProductRemoveBasket

@Location(DELETE_PRODUCT)
class ProductDeleteRoute

@Location(PRODUCT)
class ProductGetRoute

@Location(GET_ID_PRODUCT)
class ProductGetIdRoute

fun Route.ProductRoutes(
    db:Repo,
    hashFunction:(String) -> String
) {
    authenticate("jwt") {

        post<ProductCreateRoute> {

            val product = try {
                call.receive<Product>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }

            try {
                db.addProduct(product)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Product Added"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem"))
            }
        }
            //Корзина
        post<ProductCreateBasket> {
            val productId = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }
            try {
                val Cart = call.receive<Cart>()
                val product = Product("", "", "", "", "", "", Cart.id,"")
                db.addBasketProduct(product, productId)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "User Updated Successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occurred!"))
            }
        }
        get<ProductGetBasket> {
            try {
                val product = db.getBasketProduct()
                call.respond(HttpStatusCode.OK,product)
            }catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Product>())
            }
        }
        post<ProductRemoveBasket> {
            val cartId =try {
                call.request.queryParameters["id"]!!
            }catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
        }
            try {
                val cart = "0"
                val product = Product("", "", "", "", "", "", cart,"")
                db.removeBasketProduct(product, cartId)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "User Updated Successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occurred!"))
            }
        }

        //Избраное
        post<ProductCreateFavorite> {
            val favoriteId = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }
            try {
                val Favorite = call.receive<Favorite>()
                val product = Product("", "", "", "", "", "", "",Favorite.id)
                db.addFavoriteProduct(product, favoriteId)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "User Updated Successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occurred!"))
            }
        }
        get<ProductGetFavorite> {
            try {
                val product = db.getFavoriteProduct()
                call.respond(HttpStatusCode.OK,product)
            }catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Product>())
            }
        }
        post<ProductRemoveFavorite> {
            val favoriteId =try {
                call.request.queryParameters["id"]!!
            }catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }
            try {
                val favorite = "0"
                val product = Product("", "", "", "", "", "", "",favorite)
                db.removeFavoriteProduct(product, favoriteId)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "User Updated Successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occurred!"))
            }
        }

        //Вывод продукта
        get<ProductGetRoute> {
            val manufacturer = try {
                call.request.queryParameters["manufacturer"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "QueryParameter:id is not present"))
                return@get
            }
            try {
                val product = db.getAllProduct(manufacturer)
                call.respond(HttpStatusCode.OK, product)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Product>())
            }
        }

        get<ProductGetIdRoute> {
            val productId = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "QueryParameter:id is not present"))
                return@get
            }

            try {

                val product = db.getIdProduct(productId)
                call.respond(HttpStatusCode.OK, product)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem Occurred!"))
            }

        }

        delete<ProductDeleteRoute> {

            val productId = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "QueryParameter:id is not present"))
                return@delete
            }
            try {
                db.deleteProduct(productId)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note Deleted Successfully!"))

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem Occurred!"))
            }
        }
    }
}
