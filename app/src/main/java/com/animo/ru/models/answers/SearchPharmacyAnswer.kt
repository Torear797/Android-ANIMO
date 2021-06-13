package com.animo.ru.models.answers

import com.animo.ru.models.Pharmacy
import java.util.*

class SearchPharmacyAnswer(
    val pharmacy: TreeMap<Int, Pharmacy>
) : BaseAnswer()