package com.application.herbafill.Model

data class MLDetailsResponse(
    val mlherbid: Int,
    val mlherbname: String,
    val mlherbdescription: String,
    val mlherbimageurl: String,
    val recommended: String,
    val risky: String
)

data class MLBenefitsResponse(
    val mlbenefitid: Int,
    val mlherbname: String,
    val mlbenefitdescription: String,
    val mlbenefitimageUrl: String
)

data class MLStepsResponse(
    val mlstepid: Int,
    val mlherbname: String,
    val mlsteptitle: String,
    val mlstepdetails: String
)
