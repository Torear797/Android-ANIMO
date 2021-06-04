package com.animo.ru.models.answers

import com.animo.ru.models.Pharmacy

class SearchPharmacyAnswer(
    val pharmacy: MutableMap<Int, Pharmacy>
) : BaseAnswer()