package com.animo.ru.models.answers

import com.animo.ru.models.GuideData

data class GuideDataAnswer (
    val data: HashMap<Int, GuideData>,
): BaseAnswer()