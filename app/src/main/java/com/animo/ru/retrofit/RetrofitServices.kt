package com.animo.ru.retrofit

import com.animo.ru.models.answers.*
import retrofit2.Call
import retrofit2.http.*

interface RetrofitServices {
    @FormUrlEncoded
    @POST("Mobileapi/loginSubmit")
    fun login(
        @Field("login") login: String?,
        @Field("password") password: String?,
        @Field("device_id") device_id: String?,
        @Field("getToken") getToken: Boolean,
        @Field("device_info") device_info: String,
        @Field("type_device") type_device: String
    ): Call<LoginAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/reLogin")
    fun reLogin(
        @Field("token") token: String,
        @Field("refreshToken") refreshToken: String,
        @Field("device_info") device_info: String,
        @Field("type_device") type_device: String
    ): Call<LoginAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/UserInfo")
    fun getUserInfo(
        @Field("token") token: String
    ): Call<UserInfoAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/getMedicationsList")
    fun getMedicationsData(
        @Field("token") token: String
    ): Call<MedicationDataAnswer>

    @GET("Mobileapi/infoPackages/{infoPackageId}")
    fun getInfoPackages(
        @Path("infoPackageId") id: Int,
        @Query("token") token: String
    ): Call<GetInfoPackageAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/getSpecAndReg")
    fun getSpecAndReg(
        @Field("token") token: String
    ): Call<GetSpecAndRegAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/getDoctorsFromRegionAndSpec")
    fun getDoctorsFromRegionAndSpec(
        @Field("token") token: String,
        @Field("idObject") idObject: Int,
        @Field("mpIds") mpIds: Int,
        @Field("specIds[]") specIds: ArrayList<Int>
    ): Call<GetDoctorsFromSpecAndReg>

    @FormUrlEncoded
    @POST("Mobileapi/sendTrackingInfo")
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
    @POST("Mobileapi/listEvents")
    fun getEvents(
        @Field("token") token: String,
        @Field("ROLES[]") ROLES: List<String>
    ): Call<GetEventsAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/changePassword")
    fun changePassword(
        @Field("token") token: String,
        @Field("cur_password") cur_password: String,
        @Field("new_password") new_password: String,
        @Field("repeat_password") repeat_password: String
    ): Call<BaseAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/listPlans")
    fun getPlans(
        @Field("token") token: String
    ): Call<GetPlansAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/deletePlan")
    fun deletePlan(
        @Field("token") token: String,
        @Field("planId") planId: Int
    ): Call<BaseAnswer>
}