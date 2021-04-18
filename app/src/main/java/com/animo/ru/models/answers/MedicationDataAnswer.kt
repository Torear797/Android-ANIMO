package com.animo.ru.models.answers

import com.animo.ru.models.LastInfoPackage
import com.animo.ru.models.Medication

data class MedicationDataAnswer(
    val arPreparations: MutableMap<Int, Medication>,
    val arLastInfoPackage: MutableMap<Int, LastInfoPackage>
) : BaseAnswer()