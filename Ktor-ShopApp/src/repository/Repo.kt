package com.example.repository

import com.example.data.model.Product
import com.example.data.model.User
import com.example.data.table.ProductTable
import com.example.data.table.ProductTable.cartid
import com.example.data.table.ProductTable.description
import com.example.data.table.ProductTable.favoriteid
import com.example.data.table.ProductTable.id
import com.example.data.table.ProductTable.image
import com.example.data.table.ProductTable.manufacturer
import com.example.data.table.ProductTable.model
import com.example.data.table.ProductTable.price
import com.example.data.table.UserTable
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*


class Repo() {


    suspend fun addUser(user:User){
        dbQuery{
            UserTable.insert { ut->
                ut[UserTable.email] = user.email
                ut[UserTable.password] = user.password
                ut[UserTable.firstName] = user.firstName
                ut[UserTable.lastName] = user.lastName
                ut[UserTable.phoneNumber] = user.phoneNumber
            }
        }
    }


    suspend fun findUserByEmail(email:String) = dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row:ResultRow?):User?{
        if(row == null){
            return null
        }

        return User(
            email =  row[UserTable.email],
            password = row[UserTable.password],
            firstName = row[UserTable.firstName],
            lastName = row[UserTable.lastName],
            phoneNumber = row[UserTable.phoneNumber]
        )
    }

    suspend fun updatePasswordUser(user: User, oldPassword: String){
        dbQuery {
            UserTable.update(
                where = {
                   UserTable.password.eq(oldPassword)
                }
            ) {ut->
                ut[UserTable.password] = user.password
                }
            }
        }

    suspend fun updateUser(user: User, email: String){
        dbQuery {
            UserTable.update(
                where = {
                    UserTable.email.eq(email)
                }
            ){ut->
                ut[UserTable.email] = user.email
                ut[UserTable.firstName] = user.firstName
                ut[UserTable.lastName] = user.lastName
            }
        }
    }

    /*suspend fun addUser(user:User){
        dbQuery{
            UserTable.insert { ut->
                ut[UserTable.email] = user.email
                ut[UserTable.password] = user.password
                ut[UserTable.firstName] = user.firstName
                ut[UserTable.lastName] = user.lastName
                ut[UserTable.phoneNumber] = user.phoneNumber
            }
        }
    }*/


    // ==============Product===================//

 suspend fun  addProduct(product: Product){
     dbQuery {
         ProductTable.insert { pd ->
             pd[ProductTable.id] = product.id
             pd[ProductTable.manufacturer] = product.manufacturer
             pd[ProductTable.description] = product.description
             pd[ProductTable.price] = product.price
             pd[ProductTable.model] = product.model
             pd[ProductTable.image] = product.image
         }


     }
 }


    suspend fun  deleteProduct(id: String){
        dbQuery {
            ProductTable.deleteWhere { ProductTable.id.eq(id) }
        }
    }
    suspend fun getAllProduct(manufacturer:String): List<Product> = dbQuery {

        ProductTable.select{ProductTable.manufacturer.eq(manufacturer)}.map(::rowToProduct)

        }

    suspend fun getIdProduct(id:String):List<Product> = dbQuery {

        ProductTable.select {
            ProductTable.id.eq(id)
        }.mapNotNull { rowToProduct(it) }

    }
    //Корзина
    suspend fun addBasketProduct(product: Product, id: String){
        dbQuery {
            ProductTable.update(where = {
                ProductTable.id.eq(id)
            }) { it->
                it[ProductTable.cartid] = product.cartid
            }
        }
        }
    suspend fun getBasketProduct():List<Product> = dbQuery {
        ProductTable.select {
            ProductTable.id.eq(cartid)
        }
            .mapNotNull { rowToProduct(it) }

    }
    suspend fun removeBasketProduct(product: Product,id: String){
        dbQuery {
            ProductTable.update(
                where = {ProductTable.cartid.eq(id)
            }){ ut->
                ut[ProductTable.cartid]= product.cartid
            }
        }
    }
    //Избраное
    suspend fun addFavoriteProduct(product: Product, id: String){
        dbQuery {
            ProductTable.update(where = {
                ProductTable.id.eq(id)
            }) { it->
                it[ProductTable.favoriteid] = product.favoriteid
            }
        }
    }
    suspend fun getFavoriteProduct():List<Product> = dbQuery {
        ProductTable.select {
            ProductTable.id.eq(favoriteid)
        }
            .mapNotNull { rowToProduct(it) }

    }
    suspend fun removeFavoriteProduct(product: Product,id: String){
        dbQuery {
            ProductTable.update(
                where = {ProductTable.favoriteid.eq(id)
                }){ ut->
                ut[ProductTable.favoriteid]= product.favoriteid
            }
        }
    }

    /*
    private fun rowToBasketProduct(row: ResultRow) = Cart(
        id = row[id],
        userEmail = row[CartTable.userEmail]
    )*/


/*suspend fun  addProduct(product: Product){
     dbQuery {
         ProductTable.insert { pd ->
             pd[ProductTable.id] = product.id
             pd[ProductTable.manufacturer] = product.manufacturer
             pd[ProductTable.description] = product.description
             pd[ProductTable.price] = product.price
             pd[ProductTable.model] = product.model
             pd[ProductTable.image] = product.image
         }


     }
 }
}*/
    private fun rowToProduct(row: ResultRow) = Product(
            id = row[id],
            manufacturer = row[manufacturer],
            description = row[description],
            price = row[price],
            model = row[model],
            image = row[image],
            cartid = row[cartid],
            favoriteid = row[favoriteid]

        )

}