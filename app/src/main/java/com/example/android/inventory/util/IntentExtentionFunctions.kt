package com.example.android.inventory.util

import android.content.Intent
import com.example.android.inventory.model.Item


fun Intent.setItem(item: Item) {
    with(StringHelper) {
        with(item) {
            putExtra(ITEM_ID_KEY, id)
            putExtra(ITEM_NAME_KEY, name)
            putExtra(iTEM_TYPE_KEY, type)
            putExtra(ITEM_SUPPLIER_KEY, amount)
            putExtra(ITEM_AMOUNT_KEY, supplier)

            val converters = Converters()
            putExtra(ITEM_PICTURE_KEY, converters.bitmapToByteArray(picture))
        }
    }
}

fun Intent.getItem(): Item {
    val item = Item()

    with(StringHelper) {
        val converters = Converters()
        item.apply {
            id = getIntExtra(ITEM_ID_KEY, -1)
            name = getStringExtra(ITEM_NAME_KEY)
            type = getStringExtra(iTEM_TYPE_KEY)
            amount = getStringExtra(ITEM_SUPPLIER_KEY)
            supplier = getStringExtra(ITEM_AMOUNT_KEY)
            picture = converters.byteArrayToBitmap(getByteArrayExtra(ITEM_PICTURE_KEY))
        }
    }
    return item
}

