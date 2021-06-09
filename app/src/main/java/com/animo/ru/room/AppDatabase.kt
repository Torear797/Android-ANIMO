package com.animo.ru.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.animo.ru.models.LastInfoPackage
import com.animo.ru.models.Medication
import com.animo.ru.room.my_dao.LastInfoPackageDao
import com.animo.ru.room.my_dao.MedicationsDao

@Database(entities = [LastInfoPackage::class, Medication::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lastInfoPackageDao(): LastInfoPackageDao?
    abstract fun medicationsDao(): MedicationsDao?

    companion object {

        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "ANIMO"
                    ).build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }

    }
}