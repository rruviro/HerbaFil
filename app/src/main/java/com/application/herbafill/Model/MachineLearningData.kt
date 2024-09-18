package com.application.herbafill.Model

data class MLDetailsResponse(
    val mlHerbId: Int,
    val mlHerbName: String,
    val mlHerbDescription: String,
    val mlHerbImageUrl: String
)

data class MLBenefitsResponse(
    val mlBenefitId: Int,
    val mlHerbName: String,
    val mlBenefitDescription: String,
    val mlBenefitImageUrl: String
)

data class MLStepsResponse(
    val mlStepId: Int,
    val mlHerbName: String,
    val mlStepTitle: String,
    val mlStepDetails: String
)
