package com.application.herbafill.Api

import com.application.herbafill.Model.Account
import com.application.herbafill.Model.HerbalBenefitsResponse
import com.application.herbafill.Model.HerbalDetailResponse
import com.application.herbafill.Model.HerbalStepsResponse
import com.application.herbafill.Model.Authentication.LoginResponse
import com.application.herbafill.Model.Authentication.SignUpResponse
import com.application.herbafill.Model.Herbals
import com.application.herbafill.Model.MLBenefitsResponse
import com.application.herbafill.Model.MLDetailsResponse
import com.application.herbafill.Model.MLStepsResponse
import com.application.herbafill.Model.UpdateResponse
import com.application.herbafill.Model.UserHistory
import com.application.herbafill.Model.UserHistoryResponse
import com.application.herbafill.Model.UserProfile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("user")
    fun getUserDetails(@Query("userID") userID: Int): Call<Account>

    @GET("getUserHistory")
    fun getUserHistory(@Query("userid") userID: Int): Call<UserHistoryResponse>

    @FormUrlEncoded
    @POST("updateInfo")
    fun updateUserDetails(
        @Field("userID") userID: Int,
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<UpdateResponse>

    data class UploadImageRequest(
        val userprofile: String,  // Base64 image string
        val userid: Int           // User ID
    )

    @POST("update_userprofile")
    fun uploadImage(
        @Body uploadImageRequest: UploadImageRequest
    ): Call<UpdateResponse>

    @FormUrlEncoded
    @POST("createAcc")
    fun signUp(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<SignUpResponse>

    @POST("insertUserHistory")
    fun insertUserHistory(
        @Body userHistory: UserHistory
    ): Call<UserHistory>

    @GET("getHerbalDetails")
    fun getHerbalDetails(@Query("herbid") herbId: Int): Call<List<HerbalDetailResponse>>

    @GET("getHerbalBenefits")
    fun getHerbalBenefits(@Query("herbid") herbId: Int): Call<List<HerbalBenefitsResponse>>

    @GET("getHerbalSteps")
    fun getHerbalSteps(@Query("herbid") herbId: Int): Call<List<HerbalStepsResponse>>

    @GET("herbs")
    fun getHerbs(): Call<List<Herbals>>

    @GET("ml_getHerbalDetails")
    fun getHerbalDetailsByName(@Query("mlherbname") mlHerbName: String): Call<List<MLDetailsResponse>>

    @GET("ml_getHerbalBenefits")
    fun getHerbalBenefitsByName(@Query("mlherbname") mlHerbName: String): Call<List<MLBenefitsResponse>>

    @GET("ml_getHerbalSteps")
    fun getHerbalStepsByName(@Query("mlherbname") mlHerbName: String): Call<List<MLStepsResponse>>

}