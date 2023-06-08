package com.example.routes

import com.example.authentication.JwtService
import com.example.data.model.*
import com.example.repository.Repo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.ushortLiteral


const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"
const val UPDATE_PASSWORD ="$USERS/update/password"
const val UPDATE_USER = "$USERS/update"

@Location(REGISTER_REQUEST)
class UserRegisterRoute
@Location(LOGIN_REQUEST)
class UserLoginRoute
@Location(UPDATE_PASSWORD)
class UserUpdatePassword
@Location(UPDATE_USER)
class UserUpdateProfile

fun Route.UserRoutes(
    db:Repo,
    jwtService: JwtService,
    hashFunction: (String)-> String
){
    post<UserRegisterRoute> {
        val registerRequest = try {
            call.receive<RegisterRequest>()
        } catch (e:Exception){
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false,"Missing Some Fields"))
            return@post
        }

        try {
            val user = User(registerRequest.email,hashFunction(registerRequest.password),registerRequest.firstName, registerRequest.lastName, registerRequest.phoneNumber)
            db.addUser(user)
            call.respond(HttpStatusCode.OK,SimpleResponse(true,jwtService.generateToken(user)))
        }catch (e:Exception){
            call.respond(HttpStatusCode.Conflict,SimpleResponse(false,e.message ?: "Some Problem Occurred!"))
        }
    }

    post<UserLoginRoute> {
        val loginRequest = try {
            call.receive<LoginRequest>()

        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Some Fields"))
            return@post
        }

        try {
            val user = db.findUserByEmail(loginRequest.email)

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Wrong Email Id"))
            } else {

                if (user.password == hashFunction(loginRequest.password)) {
                    call.respond(
                        HttpStatusCode.OK,
                        UserService(user.firstName, user.lastName, user.email, user.phoneNumber, token = jwtService.generateToken(user))
                    )


                } else {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Password Incorrect!"))
                }
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occurred!"))
        }

    }
    authenticate("jwt") {
        post<UserUpdatePassword> {
            val userChangePassword = try {
                call.receive<UserChangePassword>()
            } catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"Missing Fields"))
                return@post
            }
            try {
                val  oldPassword = call.principal<User>()!!.password
                   val user =  User("",hashFunction(userChangePassword.password),"","","")
                db.updatePasswordUser(user,oldPassword)
                call.respond(HttpStatusCode.OK,SimpleResponse(true,"Password Updated Successfully!"))
            }catch (e:Exception){
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false,e.message ?: "Some Problem Occurred!"))
            }

        }
        post<UserUpdateProfile> {
            val userUpdate = try {
                call.receive<UserUpdate>()
            }catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"Missing Fields"))
                return@post
            }
            try {
                val email = call.principal<User>()!!.email
                val user =  User(userUpdate.email, "", userUpdate.firstName,userUpdate.lastName,userUpdate.phoneNumber)
                db.updateUser(user, email)
                call.respond(HttpStatusCode.OK,SimpleResponse(true,"User Updated Successfully!"))
            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false,e.message ?: "Some Problem Occurred!"))
            }
        }
    }

}



