package com.example.android.inventory.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.example.android.inventory.model.Item
import com.example.android.inventory.util.Converters

@Database(entities = [(Item::class)], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemsDao(): ItemsDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "items.db"

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
            }
            return INSTANCE
        }
    }
}