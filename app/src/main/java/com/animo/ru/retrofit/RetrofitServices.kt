package com.animo.ru.retrofit

import com.animo.ru.models.answers.LoginAnswer
import com.animo.ru.models.answers.UserInfoAnswer
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitServices {
    @FormUrlEncoded
    @POST("User/loginSubmit")
    fun login(
        @Field("login") login: String?,
        @Field("password") password: String?,
        @Field("device_id") device_id: String?,
        @Field("getToken") getToken: Boolean,
        @Field("remember_me") remember_me: String
    ): Call<LoginAnswer>

    @FormUrlEncoded
    @POST("User/info")
    fun getUserInfo(
        @Field("token") token: String
    ): Call<UserInfoAnswer>
}