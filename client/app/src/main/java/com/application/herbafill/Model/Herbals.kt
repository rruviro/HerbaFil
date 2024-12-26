package com.application.herbafill.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Herbals(
    val herbID: Int,
    val imageUrl: String,
    val herbName: String,
    val herbDescrip: String
) : Parcelable
