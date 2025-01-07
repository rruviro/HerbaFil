package com.application.herbafill.Model.Authentication

data class LoginResponse(
    val userID: Int,
    val email: String,
    val status: String,
    val error : Boolean,
    val message: String,
    val token : String,
)

