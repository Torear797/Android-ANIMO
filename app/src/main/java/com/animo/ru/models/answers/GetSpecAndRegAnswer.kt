package com.animo.ru.models.answers

data class GetSpecAndRegAnswer(
    var regions: MutableMap<Int, String>? = null,
    var speciality: MutableMap<Int, String>? = null,
) : BaseAnswer()