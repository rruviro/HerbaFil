package com.application.herbafill.Model.Authentication

data class LoginResponse(
    val userID: Int,
    val status: String,
    val error : Boolean,
    val message: String,
    val token : String,
)

