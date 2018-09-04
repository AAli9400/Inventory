package com.example.android.inventory.presenter

import android.graphics.Bitmap
import android.support.design.widget.Snackbar
import com.example.android.inventory.R
import com.example.android.inventory.model.Item
import com.example.android.inventory.repository.ItemRepository

class ItemActivityPresenter(
        private val mItem: Item,
        private val mRepository: ItemRepository,
        private val mView: View) {

    fun loadItemToTheActivity() {
        with(mItem) {
            mView.apply {
                setItemNameText(name)
                setItemTypeText(type)
                setItemAmountText(amount)
                setItemSupplierText(supplier)
                setItemPicture(picture)
            }
        }
    }

    fun addItem() {
        mItem.apply {
            with(mView) {
                name = getItemNameText()
                type = getItemTypeText()
                amount = getItemAmountText()
                supplier = getItemSupplierText()
                picture = getItemPicture()
            }
        }
        mRepository.addItem(mItem)
    }

    fun editItem() {
        mItem.apply {
            with(mView) {
                name = getItemNameText()
                type = getItemTypeText()
                amount = getItemAmountText()
                supplier = getItemSupplierText()
                picture = getItemPicture()
            }
        }
        mRepository.updateItem(mItem)
    }

    fun deleteItem() {
        mView.showAlertDialogue(
                mView.getString(R.string.delete_question),
                false
        ) { mRepository.deleteItem(mItem) }
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

        fun getItemNameText(): String
        fun getItemTypeText(): String?
        fun getItemAmountText(): String?
        fun getItemSupplierText(): String?
        fun getItemPicture(): Bitmap?

        fun showSnakeBar(message: String, length: Int)
        fun showAlertDialogue(message: String,
                              isCancellable: Boolean,
                              positiveButtonClickListener: () -> Unit = {})

        fun getString(id: Int): String
    }
}