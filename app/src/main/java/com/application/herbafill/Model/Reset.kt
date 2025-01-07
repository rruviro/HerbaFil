package com.application.herbafill.Model

data class ResetPasswordRequest(
    val email: String,
    val newPassword: String
)

data class ResetPasswordResponse(
    val message: String
)
