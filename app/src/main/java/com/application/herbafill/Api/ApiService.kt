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
    @POST("login.php")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("fetchable/getInformation.php")
    fun getUserDetails(@Query("userID") userID: Int): Call<Account>

    @GET("fetchable/history_details/fetch.php")
    fun getUserHistory(@Query("userID") userID: Int): Call<UserHistoryResponse>

    @FormUrlEncoded
    @POST("updates/updateInfo.php")
    fun updateUserDetails(
        @Field("userID") userID: Int,
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<UpdateResponse>

    @Multipart
    @POST("upload_image1.php")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("id") userId: RequestBody
    ): Call<UpdateResponse>

    @FormUrlEncoded
    @POST("createAcc.php")
    fun signUp(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<SignUpResponse>

    @POST("insertion/history_details/insert.php")
    fun insertUserHistory(
        @Body userHistory: UserHistory
    ): Call<UserHistory>

    @GET("insertion/herbs_details/insert.php?action=getHerbalDetails")
    fun getHerbalDetails(@Query("herbId") herbId: Int): Call<List<HerbalDetailResponse>>

    @GET("insertion/herbs_details/insert.php?action=getHerbalBenefits")
    fun getHerbalBenefits(@Query("herbId") herbId: Int): Call<List<HerbalBenefitsResponse>>

    @GET("insertion/herbs_details/insert.php?action=getHerbalSteps")
    fun getHerbalSteps(@Query("herbId") herbId: Int): Call<List<HerbalStepsResponse>>

    @GET("fetchable/commonHerbs.php")
    fun getHerbs(): Call<List<Herbals>>

    @GET("insertion/mlHerbs_details/insert.php?action=getHerbalDetails")
    fun getHerbalDetailsByName(@Query("mlHerbName") mlHerbName: String): Call<List<MLDetailsResponse>>

    @GET("insertion/mlHerbs_details/insert.php?action=getHerbalBenefits")
    fun getHerbalBenefitsByName(@Query("mlHerbName") mlHerbName: String): Call<List<MLBenefitsResponse>>

    @GET("insertion/mlHerbs_details/insert.php?action=getHerbalSteps")
    fun getHerbalStepsByName(@Query("mlHerbName") mlHerbName: String): Call<List<MLStepsResponse>>

}