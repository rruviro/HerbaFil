package com.application.herbafill.Model

data class UpdateResponse(
    val status: String,
    val message: String,
    val user: Account?,
)

data class Account(
    val userID: Int,
    val name: String,
    val username: String,
    val password: String,
    val userProfile: String,
)

data class UserProfileUpdateRequest(
    val userId: String,
    val userProfile: String
)

data class CreateAccountRequest(
    val name: String,
    val email: String,
    val username: String,
    val password: String
)

data class CreateAccountResponse(
    val success: Boolean,
    val message: String
)

data class UserImage(
    val email: String = "",
    val userProfile: String = ""
)