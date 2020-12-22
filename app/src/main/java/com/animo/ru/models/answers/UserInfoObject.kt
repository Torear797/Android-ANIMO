package com.animo.ru.models.answers

import com.animo.ru.models.Direction
import com.animo.ru.models.Role

class UserInfoObject(
    var first_name: String? = null,
    var surname: String? = null,
    var patronymic: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var direction: Direction? = null,
    var role: List<Role>,
    var regions: MutableMap<Int, String>? = null,
    var create_date: String? = null
)