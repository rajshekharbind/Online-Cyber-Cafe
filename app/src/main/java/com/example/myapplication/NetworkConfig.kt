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

// ── Req 52: User-Friendly Error Handling ──────────────────────────────
/**
 * Never surface raw HTTP/system errors to the user.
 * Map all error codes to friendly, actionable strings.
 */
object AppError {
    fun userMessage(throwable: Throwable): String = when {
        throwable.message?.contains("401") == true ->
            "Incorrect email or password. Please try again."
        throwable.message?.contains("403") == true ->
            "You don't have permission to do this. Contact support."
        throwable.message?.contains("404") == true ->
            "The requested information was not found. Please refresh."
        throwable.message?.contains("409") == true ->
            "A duplicate entry already exists. Please check your details."
        throwable.message?.contains("429") == true ->
            "Too many requests. Please wait a moment and try again."
        throwable.message?.contains("5") == true ->
            "Our servers are busy right now. Please try again in a few minutes."
        throwable.message?.contains("UnknownHost") == true ||
        throwable.message?.contains("timeout") == true ->
            "No internet connection. Please check your network settings."
        else ->
            "Something went wrong. Please try again or contact support."
    }
}
