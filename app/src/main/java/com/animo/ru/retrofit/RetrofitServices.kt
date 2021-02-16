package com.animo.ru.retrofit

import com.animo.ru.models.answers.*
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
    @POST("MobileApi/UserInfo")
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
        @Query("ROLES[]") ROLES: List<String>
    ): Call<GetInfoPackageAnswer>

    @FormUrlEncoded
    @POST("MobileApi/getSpecAndReg")
    fun getSpecAndReg(
        @Field("token") token: String
    ): Call<GetSpecAndRegAnswer>

    @FormUrlEncoded
    @POST("preparatmanagement/getDoctorsFromRegionAndSpec")
    fun getDoctorsFromRegionAndSpec(
        @Field("token") token: String,
        @Field("idObject") idObject: Int,
        @Field("mpIds") mpIds: Int,
        @Field("specIds[]") specIds: ArrayList<Int>
    ): Call<GetDoctorsFromSpecAndReg>

    @FormUrlEncoded
    @POST("Sharing/sendTrackingInfo")
    fun sendTrackingInfo(
        @Field("token") token: String,
        @Field("typeBtn") typeBtn: String,
        @Field("typeDevice") typeDevice: String,
        @Field("idObject") idObject: Int,
        @Field("typeObject") typeObject: String,
        @Field("idDoctor") idDoctor: Int,
        @Field("screenResolution") screenResolution: String,
        @Field("deviceInfo") deviceInfo: String,
    ): Call<BaseAnswer>

    @FormUrlEncoded
    @POST("Events/listEvents")
    fun getEvents(
        @Field("token") token: String,
        @Field("ROLES[]") ROLES: List<String>
    ): Call<GetEventsAnswer>
}