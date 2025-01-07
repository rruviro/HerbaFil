package com.application.herbafill.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class UserHistory(
    val userID: Int,
    val mlherbname: String
)

@Parcelize
data class UserHistoryDetail(
    val mlherbname: String,
    val mllimiteddescript: String,
    val mlherbimageurl: String
) : Parcelable

data class UserHistoryResponse(
    val data: List<UserHistoryDetail>?,
    val error: String?,
    val message: String?
)
