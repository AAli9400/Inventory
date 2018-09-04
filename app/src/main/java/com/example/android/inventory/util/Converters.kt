package com.example.android.inventory.util

import android.arch.persistence.room.TypeConverter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class Converters {
    @TypeConverter
    fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)

            return stream.toByteArray()
        }
        return null
    }

    @TypeConverter
    fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
        if (byteArray != null) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
        return null
    }
}