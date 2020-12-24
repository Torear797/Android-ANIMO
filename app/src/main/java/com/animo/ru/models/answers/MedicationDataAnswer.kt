package com.animo.ru.models.answers

import com.animo.ru.models.LastInfoPackage
import com.animo.ru.models.Medication

data class MedicationDataAnswer(
    var arPreparations: MutableMap<Int, Medication>,
    var arLastInfoPackage: MutableMap<Int, LastInfoPackage>
) : BaseAnswer()