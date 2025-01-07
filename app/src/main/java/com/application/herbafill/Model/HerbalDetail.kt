package com.application.herbafill.Model

data class HerbalDetailResponse(
    val herbid: Int,
    val herbname: String,
    val herbdescription: String,
    val herbimage: String,
    val recommended: String,
    val risky: String
)

data class HerbalBenefitsResponse(
    val benefitid: Int,
    val herbid: Int,
    val benefitdescription: String,
    val benefitimageurl: String
)

data class HerbalStepsResponse(
    val stepId: Int,
    val herbid: Int,
    val steptitle: String,
    val stepdetails: String
)