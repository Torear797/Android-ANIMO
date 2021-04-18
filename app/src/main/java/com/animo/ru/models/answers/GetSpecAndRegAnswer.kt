package com.animo.ru.models.answers

data class GetSpecAndRegAnswer(
    val regions: MutableMap<Int, String>? = null,
    val speciality: MutableMap<Int, String>? = null,
) : BaseAnswer()