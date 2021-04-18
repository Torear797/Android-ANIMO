package com.animo.ru.models.answers

data class LoginAnswer(
    val token: String? = null,
    val exp: String? = null,
    val refreshToken: String? = null,
    val refreshExp: String? = null,
    val user_id: Int? = null
) : BaseAnswer()