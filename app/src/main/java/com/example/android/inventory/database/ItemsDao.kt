package com.example.android.inventory.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.support.annotation.NonNull
import com.example.android.inventory.model.Item

@Dao
interface ItemsDao {
    @Query("SELECT * FROM Item")
    fun getAllItems(): LiveData<List<Item>>

    @Insert
    fun addItem(@NonNull item: Item): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateItem(@NonNull item: Item): Int

    @Delete
    fun deleteItem(@NonNull item: Item): Int

    @Query("DELETE FROM Item")
    fun deleteAllItems()
}