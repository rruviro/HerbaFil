package com.application.herbafill.Api

import com.application.herbafill.Model.Authentication.HerbalBenefitsResponse
import com.application.herbafill.Model.Authentication.HerbalDetailResponse
import com.application.herbafill.Model.Authentication.HerbalStepsResponse
import com.application.herbafill.Model.Authentication.LoginResponse
import com.application.herbafill.Model.Authentication.SignUpResponse
import com.application.herbafill.Model.Herbals
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("createAcc.php")
    fun signUp(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<SignUpResponse>

    @GET("fetchable/commonHerbs.php")
    fun getHerbsByName(@Query("herbName") herbId: String): Call<List<Herbals>>

    @GET("insertion/herbs_details/insert.php?action=getHerbalDetails")
    fun getHerbalDetails(@Query("herbId") herbId: Int): Call<List<HerbalDetailResponse>>

    @GET("insertion/herbs_details/insert.php?action=getHerbalBenefits")
    fun getHerbalBenefits(@Query("herbId") herbId: Int): Call<List<HerbalBenefitsResponse>>

    @GET("insertion/herbs_details/insert.php?action=getHerbalSteps")
    fun getHerbalSteps(@Query("herbId") herbId: Int): Call<List<HerbalStepsResponse>>

    @GET("fetchable/commonHerbs.php")
    fun getHerbs(): Call<List<Herbals>>

}