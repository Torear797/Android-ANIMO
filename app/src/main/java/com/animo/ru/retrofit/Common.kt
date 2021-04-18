package com.animo.ru.retrofit

object Common {
    private const val BASE_URL = "https://dev.animo.su/"
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}