package com.animo.ru.models

class DoctorData(
    val doctorId: Int,
    val surname: String,
    val name: String,
    var patronymic: String,
    val region: String,
    val city: String,
    val specialization: String
)