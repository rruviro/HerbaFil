package com.application.herbafill.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class UserHistory(
    val userID: Int,
    val mlHerbName: String
)

@Parcelize
data class UserHistoryDetail(
    val mlHerbName: String,
    val mlLimitedDescript: String,
    val mlHerbImageUrl: String
) : Parcelable

data class UserHistoryResponse(
    val data: List<UserHistoryDetail>?,
    val error: String?,
    val message: String?
)
