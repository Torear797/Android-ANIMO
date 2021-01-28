package com.animo.ru.models.answers

import com.animo.ru.models.InfoPackage

data class GetInfoPackageAnswer(
    var packages: MutableMap<Int, InfoPackage>
) : BaseAnswer()