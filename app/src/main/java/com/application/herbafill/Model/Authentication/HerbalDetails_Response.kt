package com.application.herbafill.Model.Authentication

data class HerbalDetailResponse(
    val herbId: Int,
    val herbName: String,
    val herbDescription: String,
    val herbImage: String
)

data class HerbalBenefitsResponse(
    val benefitId: Int,
    val herbId: Int,
    val benefitDescription: String,
    val benefitImageUrl: String
)

data class HerbalStepsResponse(
    val stepId: Int,
    val herbId: Int,
    val stepTitle: String,
    val stepDetails: String
)