package com.animo.ru.models.answers

data class LoginAnswer(
    var token: String? = null,
    var exp: String? = null,
    var refreshToken: String? = null,
    var refreshExp: String? = null,
    var user_id: Int? = null
) : BaseAnswer()