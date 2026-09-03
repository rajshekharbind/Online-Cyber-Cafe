package com.example.myapplication

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object NetworkConfig {
    private var baseUrl = "http://10.0.2.2:8080/api/v1/"
    
    // Allows you to change IP at runtime if you are on a real device
    fun setServerIp(ip: String) {
        baseUrl = "http://$ip:8080/api/v1/"
        rebuildRetrofit()
    }

    private var _retrofit: Retrofit? = null
    
    private fun rebuildRetrofit() {
        _retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthServiceApi
        get() {
            if (_retrofit == null) rebuildRetrofit()
            return _retrofit!!.create(AuthServiceApi::class.java)
        }
}

// Local Database for Demo Mode (When backend is off)
object LocalDb {
    private var registeredUsers = mutableListOf<String>()
    
    fun registerLocally(email: String) {
        registeredUsers.add(email)
    }
    
    fun isRegistered(email: String): Boolean = registeredUsers.contains(email)
}

interface AuthServiceApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("auth/authenticate")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto
}

data class RegisterRequestDto(val email: String, val password: String, val role: String)
data class LoginRequestDto(val email: String, val password: String)
data class AuthResponseDto(val token: String, val role: String? = null)
