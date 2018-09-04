package com.example.android.inventory.ui

import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar

class SnakeBarCallback(private val onDismissed: () -> Unit)
    : BaseTransientBottomBar.BaseCallback<Snackbar>() {

    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) = onDismissed()
}