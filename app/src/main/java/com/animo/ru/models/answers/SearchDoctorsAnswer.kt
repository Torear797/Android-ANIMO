package com.animo.ru.models.answers

import com.animo.ru.models.Doctor
import java.util.*

data class SearchDoctorsAnswer(
    val doctors: TreeMap<Int, Doctor>
) : BaseAnswer()