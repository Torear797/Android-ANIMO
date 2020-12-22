package com.animo.ru.models

import java.io.Serializable

data class Plan (
    val id:Int = 0,
    var data: String = "",
    var user: String = "",
    var description: String = "",
) : Serializable