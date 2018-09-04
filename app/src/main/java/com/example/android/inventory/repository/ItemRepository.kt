package com.example.android.inventory.repository

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import com.example.android.inventory.database.AppDatabase
import com.example.android.inventory.model.Item

class ItemRepository(application: Application) {
    private val itemsDao = AppDatabase.getInstance(application)?.itemsDao()
    private val items = itemsDao?.getAllItems()

    fun getAllItems() = items

    fun addItem(item: Item) {
        DatabaseAsyncTask { itemsDao?.addItem(item) }.execute()
    }

    fun updateItem(item: Item) {
        DatabaseAsyncTask { itemsDao?.updateItem(item) }.execute()
    }

    fun deleteItem(item: Item) {
        DatabaseAsyncTask { itemsDao?.deleteItem(item) }.execute()
    }

    fun deleteAllItems() {
        DatabaseAsyncTask { itemsDao?.deleteAllItems() }.execute()
    }

    class DatabaseAsyncTask(val body: () -> Unit) : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg p0: Unit?) {
            body()
        }
    }
}
