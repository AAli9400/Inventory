package com.example.android.inventory.presenter

import android.graphics.Bitmap
import android.support.design.widget.Snackbar
import com.example.android.inventory.R
import com.example.android.inventory.model.Item
import com.example.android.inventory.repository.ItemRepository

class ItemActivityPresenter(
        private val mRepository: ItemRepository,
        private val mView: View) {

    fun loadItemToTheActivity(item: Item) {
        with(item) {
            mView.apply {
                setItemNameText(name)
                setItemTypeText(type)
                setItemAmountText(amount)
                setItemSupplierText(supplier)
                setItemPicture(picture)
            }
        }
    }

    fun addItem(item: Item) {
        mRepository.addItem(item)
    }

    fun editItem(item: Item) {
        mRepository.updateItem(item)
    }

    fun deleteItem(item: Item) {
        mView.showAlertDialogue(
                mView.getString(R.string.delete_question),
                false
        ) { mRepository.deleteItem(item) }
    }

    fun confirmExitingWithoutSave() {
        mView.showAlertDialogue(
                mView.getString(R.string.discard_question),
                false)
    }

    fun warningAboutRequiredItemName() {
        mView.showSnakeBar(mView.getString(R.string.item_name_required), Snackbar.LENGTH_SHORT)
    }

    interface View {
        fun setItemNameText(name: String)
        fun setItemTypeText(type: String?)
        fun setItemAmountText(amount: String?)
        fun setItemSupplierText(supplier: String?)
        fun setItemPicture(picture: Bitmap?)

        fun showSnakeBar(message: String, length: Int)
        fun showAlertDialogue(message: String,
                              isCancellable: Boolean,
                              positiveButtonClickListener: () -> Unit = {})

        fun getString(id: Int): String
    }
}