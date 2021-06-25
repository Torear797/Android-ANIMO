package com.animo.ru.retrofit

object Common {
//    private const val BASE_URL = "https://dev.animo.su/"
    private const val BASE_URL = "http://192.168.0.69/"
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}