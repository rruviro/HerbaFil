package com.application.herbafill.Model

data class OTPRequest(val email: String)
data class OTPResponse(val message: String, val otp: String)

data class OTPVerifyRequest(val email: String, val otp: String)
data class OTPVerifyResponse(val message: String)

data class OTPForgotVerifyRequest(val email: String, val otp: String)
data class OTPForgotVerifyResponse(val message: String)