package com.animo.ru.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Medication (
    @PrimaryKey
    val id: Long = 0,
    val name: String = "",
    val cntInfPack: Int = 0
)