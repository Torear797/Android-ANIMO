package com.animo.ru.models.answers

import com.animo.ru.models.Plan

data class GetPlansAnswer(
    val arrPlans: MutableMap<Int, Plan>
) : BaseAnswer()