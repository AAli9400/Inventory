package com.example.android.inventory.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class SwipeToDeleteCallBack(dragDirection: Int, swipeDirection: Int, val body: (Int) -> Unit)
    : ItemTouchHelper.SimpleCallback(dragDirection, swipeDirection) {

    override fun onMove(recyclerView: RecyclerView, holder1: RecyclerView.ViewHolder, holder2: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(holder: RecyclerView.ViewHolder, p1: Int) {
        body(holder.adapterPosition)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Get RecyclerView item from the ViewHolder
            val itemView = viewHolder.itemView

            val p = Paint()
            p.color = Color.RED
            if (dX > 0) {
                //RIGHT
                c.drawRect(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        dX,
                        itemView.bottom.toFloat(),
                        p
                )

                p.color = Color.WHITE
                p.textSize = 36F
                c.drawText(
                        "Delete",
                        dX / 4,
                        (itemView.top.toFloat() + itemView.bottom.toFloat()) / 2,
                        p
                )
            } else {
                //LEFT
                c.drawRect(
                        itemView.right.toFloat() + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        p
                )

                p.color = Color.WHITE
                p.textSize = 36F
                c.drawText(
                        "Delete",
                        itemView.right.toFloat() + dX / 4,
                        (itemView.top.toFloat() + itemView.bottom.toFloat()) / 2,
                        p
                )
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}