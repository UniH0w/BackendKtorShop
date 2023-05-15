package com.example.repository

import com.example.data.model.Product
import com.example.data.model.User
import com.example.data.table.ProductTable
import com.example.data.table.ProductTable.description
import com.example.data.table.ProductTable.id
import com.example.data.table.ProductTable.manufacturer
import com.example.data.table.ProductTable.price
import com.example.data.table.UserTable
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select


class Repo {

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

    // ==============Product===================//

 suspend fun  addProduct(product: Product, email: String){
     dbQuery {
         ProductTable.insert { pd ->
             pd[ProductTable.id] = product.id
             pd[ProductTable.userEmail] = email
             pd[ProductTable.manufacturer] = product.manufacturer
             pd[ProductTable.description] = product.description
             pd[ProductTable.price] = product.price
         }


     }
 }

    suspend fun  deleteProduct(id: String, email: String){
        dbQuery {
            ProductTable.deleteWhere { ProductTable.id.eq(id) }
        }
    }
    suspend fun getAllProduct(email: String): List<Product> = dbQuery {
        ProductTable.select{
            ProductTable.userEmail.eq(email)}
            .mapNotNull { rowToProduct(it)
            }
        }


    private fun rowToProduct(row: ResultRow?): Product?{
        if (row == null){
            return null
        }
        return Product(
            id = row[id],
            manufacturer = row[manufacturer],
            description = row[description],
            price = row[price]

        )
    }


}