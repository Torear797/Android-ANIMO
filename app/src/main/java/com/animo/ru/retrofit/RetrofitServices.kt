package com.animo.ru.retrofit

import com.animo.ru.models.Role
import com.animo.ru.models.answers.GetInfoPackageAnswer
import com.animo.ru.models.answers.LoginAnswer
import com.animo.ru.models.answers.MedicationDataAnswer
import com.animo.ru.models.answers.UserInfoAnswer
import retrofit2.Call
import retrofit2.http.*

interface RetrofitServices {
    @FormUrlEncoded
    @POST("User/loginSubmit")
    fun login(
        @Field("login") login: String?,
        @Field("password") password: String?,
        @Field("device_id") device_id: String?,
        @Field("getToken") getToken: Boolean,
        @Field("device_info") device_info: String,
        @Field("type_device") type_device: String
    ): Call<LoginAnswer>

    @FormUrlEncoded
    @POST("User/reLogin")
    fun reLogin(
        @Field("token") token: String,
        @Field("refreshToken") refreshToken: String,
        @Field("device_info") device_info: String,
        @Field("type_device") type_device: String
    ): Call<LoginAnswer>

    @FormUrlEncoded
    @POST("User/info")
    fun getUserInfo(
        @Field("token") token: String
    ): Call<UserInfoAnswer>

    @FormUrlEncoded
    @POST("PreparatManagement/list")
    fun getMedicationsData(
        @Field("token") token: String
    ): Call<MedicationDataAnswer>

    @GET("preparatmanagement/infoPackages/{infoPackageId}")
    fun getInfoPackages(
        @Path("infoPackageId") id: Int,
        @Query("token") token: String,
        @Query("AUTH_ID") AUTH_ID: Int,
        @Query("ROLES") ROLES: List<Role>
//        @Field("ROLES") ROLES: Int
    ): Call<GetInfoPackageAnswer>
}