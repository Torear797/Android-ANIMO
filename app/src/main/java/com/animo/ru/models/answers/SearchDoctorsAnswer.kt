package com.animo.ru.models.answers

import com.animo.ru.models.Doctor

data class SearchDoctorsAnswer(
    val doctors: MutableMap<Int, Doctor>
) : BaseAnswer()