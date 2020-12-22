package com.animo.ru.models.answers

data class LoginAnswer(
    var token: String? = null,
    var exp: String? = null,
    var user_id: Int? = null,
) : BaseAnswer()