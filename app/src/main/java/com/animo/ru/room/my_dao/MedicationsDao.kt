package com.animo.ru.room.my_dao

import androidx.room.*
import com.animo.ru.models.Medication

@Dao
interface MedicationsDao {
    @Query("SELECT * FROM Medication")
    fun getAll(): List<Medication>

    @Query("SELECT * FROM Medication WHERE id = :id")
    fun getById(id: Long): Medication?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<Medication>)

    @Delete
    fun delete(user: Medication)
}