package com.animo.ru.room.my_dao

import androidx.room.*
import com.animo.ru.models.LastInfoPackage

@Dao
interface LastInfoPackageDao {
    @Query("SELECT * FROM LastInfoPackage")
    fun getAll(): List<LastInfoPackage>

    @Query("SELECT * FROM LastInfoPackage WHERE id = :id")
    fun getById(id: Long): LastInfoPackage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<LastInfoPackage>)

    @Delete
    fun delete(user: LastInfoPackage)
}