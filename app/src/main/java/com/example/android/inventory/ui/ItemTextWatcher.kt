package com.example.android.inventory.ui

import android.text.Editable
import android.text.TextWatcher

class ItemTextWatcher(private val onTextChanged: (String) -> Unit) : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        onTextChanged(p0.toString())
    }
}