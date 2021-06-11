package com.animo.ru.models.answers

import com.animo.ru.models.LoyaltyForm

class RecordLoyaltyFormDataAnswer(
    val specialtyName: String,
    val loyaltyData: List<Map<String, LoyaltyForm>>
) : BaseAnswer()