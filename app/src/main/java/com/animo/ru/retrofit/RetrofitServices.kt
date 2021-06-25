package com.animo.ru.retrofit

import com.animo.ru.models.DoctorData
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

    /*@Header("Authorization") token: String,*/
    @FormUrlEncoded
    @POST("Mobileapi/reLogin")
    fun reLogin(
        @Header("Authorization") token: String,
        @Field("refreshToken") refreshToken: String,
        @Field("device_info") device_info: String,
        @Field("type_device") type_device: String
    ): Call<LoginAnswer>

    @POST("Mobileapi/UserInfo")
    fun getUserInfo(
        @Header("Authorization") token: String
    ): Call<UserInfoAnswer>

    @POST("Mobileapi/getMedicationsList")
    fun getMedicationsData(
        @Header("Authorization") token: String
    ): Call<MedicationDataAnswer>

    @GET("Mobileapi/infoPackages/{infoPackageId}")
    fun getInfoPackages(
        @Path("infoPackageId") id: Int,
        @Header("Authorization") token: String
    ): Call<GetInfoPackageAnswer>

    @POST("Mobileapi/getSpecAndReg")
    fun getSpecAndReg(
        @Header("Authorization") token: String
    ): Call<GetSpecAndRegAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/getDoctorsFromRegionAndSpec")
    fun getDoctorsFromRegionAndSpec(
        @Header("Authorization") token: String,
        @Field("idObject") idObject: Int,
        @Field("mpIds") mpIds: Int,
        @Field("specIds[]") specIds: ArrayList<Int>
    ): Call<GetDoctorsFromSpecAndReg>

    @FormUrlEncoded
    @POST("Mobileapi/sendTrackingInfo")
    fun sendTrackingInfo(
        @Header("Authorization") token: String,
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
        @Header("Authorization") token: String,
        @Field("ROLES[]") ROLES: List<String>
    ): Call<GetEventsAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/changePassword")
    fun changePassword(
        @Header("Authorization") token: String,
        @Field("cur_password") cur_password: String,
        @Field("new_password") new_password: String,
        @Field("repeat_password") repeat_password: String
    ): Call<BaseAnswer>

    @POST("Mobileapi/listPlans")
    fun getPlans(
        @Header("Authorization") token: String
    ): Call<GetPlansAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/deletePlan")
    fun deletePlan(
        @Header("Authorization") token: String,
        @Field("planId") planId: Int
    ): Call<BaseAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/searchDoctors")
    fun searchDoctors(
        @Header("Authorization") token: String,
        @FieldMap searchOptions: HashMap<String, String>
    ): Call<SearchDoctorsAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/searchPharmacy")
    fun searchPharmacy(
        @Header("Authorization") token: String,
        @FieldMap searchOptions: HashMap<String, String>
    ): Call<SearchPharmacyAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/attachDoctorByUser")
    fun attachDoctor(
        @Header("Authorization") token: String,
        @Field("docId") docId: Int
    ): Call<BaseAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/attachPharmacyByUser")
    fun attachPharmacy(
        @Header("Authorization") token: String,
        @Field("pharmacyId") pharmacyId: Int
    ): Call<BaseAnswer>

    @POST("Mobileapi/getActivityData")
    fun getActivityData(
        @Header("Authorization") token: String
    ): Call<GuideDataAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/createPlan")
    fun createPlan(
        @Header("Authorization") token: String,
        @Field("dateReport") dateReport: String,
        @Field("beginVacationDate") beginVacationDate: String,
        @Field("endVacationDate") endVacationDate: String,
        @Field("outvisitActivity") outvisitActivity: Int,
        @Field("note") note: String,
        @Field("arrDocId[]") arrDocId: ArrayList<Int>,
        @Field("arrPharmId[]") arrPharmId: ArrayList<Int>
    ): Call<BaseAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/getDoctorsSelectForLoyalty")
    fun getDoctorsSelectForLoyalty(
        @Header("Authorization") token: String,
        @Field("planId") planId: Int
    ): Call<GuideDataAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/getSegmentLoyaltyFormData")
    fun getSegmentLoyaltyFormData(
        @Header("Authorization") token: String,
        @Field("idDoctor") idDoctor: Int
    ): Call<RecordLoyaltyFormDataAnswer>

    @GET("Mobileapi/getDoctorData/{doctorId}")
    fun getDoctorData(
        @Header("Authorization") token: String,
        @Path("doctorId") id: Int
    ): Call<GetDoctorDataAnswer>

    @GET("Mobileapi/getPharmacyData/{pharmacyId}")
    fun getPharmacyData(
        @Header("Authorization") token: String,
        @Path("pharmacyId") id: Int
    ): Call<GetPharmacyDataAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/saveDoctorData")
    fun saveDoctorData(
        @Header("Authorization") token: String,
        @Field("doctorId") doctorId: Int,
        @Field("surname") surname: String,
        @Field("name") name: String,
        @Field("patronymic") patronymic: String
    ): Call<BaseAnswer>

    @FormUrlEncoded
    @POST("Mobileapi/savePharmacyData")
    fun savePharmacyData(
        @Header("Authorization") token: String,
        @Field("pharmacyId") pharmacyId: Int,
        @Field("name") name: String,
        @Field("surname") surname: String,
        @Field("first_name") first_name: String,
        @Field("patronymic") patronymic: String
    ): Call<BaseAnswer>
}