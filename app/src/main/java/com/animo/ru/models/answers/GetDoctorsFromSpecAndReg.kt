package com.animo.ru.models.answers

data class GetDoctorsFromSpecAndReg(
    val data: MutableMap<Int, ShareDoctor>? = null
) : BaseAnswer()