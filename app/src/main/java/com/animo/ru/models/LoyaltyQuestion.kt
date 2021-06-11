package com.animo.ru.models

class LoyaltyQuestion (
    val text: String,
    val value: Double,
    val isChecked: Boolean,
    val options: Map<Int, LoyaltyOption>
)