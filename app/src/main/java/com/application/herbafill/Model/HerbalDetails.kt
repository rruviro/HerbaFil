package com.application.herbafill.Model

data class HerbalBenifits(
    val imagebenifit: Int,
    val descriptionBenifit: String,
)

data class HerbalSteps(
    val stepTitle: String,
    val stepBullet: List<String>
)

data class HerbalDetail(
    val image: Int,
    val title: String,
    val description: String,
)
