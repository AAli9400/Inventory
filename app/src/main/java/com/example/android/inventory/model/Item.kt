package com.example.android.inventory.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap

@Entity
class Item{
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0
        var name: String = ""
        var type: String? = null
        var amount: String? = null
        var supplier: String? = null
        var picture: Bitmap? = null
}