package com.animo.ru.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LastInfoPackage(
    @PrimaryKey
    val id: Long = 0,
    val id_preparat: Int = 0,
    val pip_name: String = ""
)