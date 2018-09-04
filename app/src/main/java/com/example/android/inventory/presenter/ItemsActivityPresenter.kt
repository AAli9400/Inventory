package com.example.android.inventory.presenter

import android.support.design.widget.Snackbar
import android.support.v7.widget.helper.ItemTouchHelper
import com.example.android.inventory.R
import com.example.android.inventory.repository.ItemsViewModel
import com.example.android.inventory.ui.ItemsRecyclerViewAdapter
import com.example.android.inventory.ui.SwipeToDeleteCallBack

class ItemsActivityPresenter(
        private val mRecyclerViewAdapter: ItemsRecyclerViewAdapter,
        private val mViewModel: ItemsViewModel,
        private val mView: View) {

    fun confirmDeletion() {
        mView.showAlertDialogue(mView.getString(R.string.delete_all_items), false) {
            mRecyclerViewAdapter.removeAll()
            this.showSnakeBar()
        }
    }

    private fun showSnakeBar() {
        mView.showSnakeBar(
                text = mView.getString(R.string.all_items_deleted),
                length = Snackbar.LENGTH_LONG,
                callbackOnDismissed = { mViewModel.deleteAllItems() },
                actionText = mView.getString(R.string.undo),
                actionClickListener = { mRecyclerViewAdapter.setItems(mViewModel.getAllItems()?.value) }

        )
    }

    fun createSwipeToDeleteCallback(): SwipeToDeleteCallBack {
        return SwipeToDeleteCallBack(0, ItemTouchHelper.LEFT) { position ->
            val item = mRecyclerViewAdapter.getItem(position)

            item?.let {
                mRecyclerViewAdapter.removeItem(position)

                mView.showSnakeBar(
                        text = mView.getString(R.string.item_deleted),
                        length = Snackbar.LENGTH_LONG,
                        callbackOnDismissed = { mViewModel.deleteItem(it) },
                        actionText = mView.getString(R.string.undo),
                        actionClickListener = { mRecyclerViewAdapter.addItem(it, position) }
                )
            }
        }
    }

    interface View {
        fun showAlertDialogue(
                message: String,
                isCancellable: Boolean,
                positiveButtonClickListener: () -> Unit)

        fun showSnakeBar(
                text: String,
                length: Int,
                callbackOnDismissed: () -> Unit,
                actionText: String,
                actionClickListener: () -> Unit
        )

        fun getString(id: Int): String
    }
}