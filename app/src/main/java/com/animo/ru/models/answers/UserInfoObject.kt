package com.animo.ru.models.answers

import com.animo.ru.models.Direction
import com.animo.ru.models.Role

class UserInfoObject(
    val first_name: String? = null,
    val surname: String? = null,
    val patronymic: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val direction: Direction? = null,
    val role: List<Role>,
    val regions: MutableMap<Int, String>? = null,
    val create_date: String? = null
)