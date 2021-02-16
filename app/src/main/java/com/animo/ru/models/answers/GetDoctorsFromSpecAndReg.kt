package com.animo.ru.models.answers

data class GetDoctorsFromSpecAndReg(
    var data: MutableMap<Int, ShareDoctor>? = null
) : BaseAnswer()