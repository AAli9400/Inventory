package com.example.android.inventory.repository

import android.arch.lifecycle.ViewModel
import com.example.android.inventory.model.Item

class ItemViewModel : ViewModel() {
    var item = Item()
}