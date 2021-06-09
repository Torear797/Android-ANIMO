package com.animo.ru.models.answers

import com.animo.ru.models.LastInfoPackage
import com.animo.ru.models.Medication
import java.util.*

data class MedicationDataAnswer(
    val arPreparations: TreeMap<Int, Medication>,
    val arLastInfoPackage: TreeMap<Int, LastInfoPackage>
) : BaseAnswer()