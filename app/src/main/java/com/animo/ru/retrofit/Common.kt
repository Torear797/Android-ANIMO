package com.animo.ru.retrofit

object Common {
    private val BASE_URL = "http://192.168.0.69/"
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}