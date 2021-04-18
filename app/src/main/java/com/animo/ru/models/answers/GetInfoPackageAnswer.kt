package com.animo.ru.models.answers

import com.animo.ru.models.InfoPackage

data class GetInfoPackageAnswer(
    val packages: MutableMap<Int, InfoPackage>
) : BaseAnswer()