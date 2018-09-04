package com.example.android.inventory.repository

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import com.example.android.inventory.model.Item
import com.example.android.inventory.repository.ItemRepository

class ItemsViewModel(application: Application) : AndroidViewModel(Application()) {
    private val mRepository = ItemRepository(application)
    private val mItems = mRepository.getAllItems()

    fun getAllItems() = mItems

    fun deleteItem(item: Item) {
        mRepository.deleteItem(item)
    }

    fun deleteAllItems() {
        mRepository.deleteAllItems()
    }
}