package com.animo.ru.models

class LoyaltyForm(
    val id: String,
    val questions: Map<Int, LoyaltyQuestion>
)