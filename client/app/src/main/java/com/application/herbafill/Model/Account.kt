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

data class UserProfile(
    val userID: Int,
    val userImage: String
)
