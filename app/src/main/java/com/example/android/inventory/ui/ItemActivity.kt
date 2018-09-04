package com.example.android.inventory.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.example.android.inventory.R
import com.example.android.inventory.model.Item
import com.example.android.inventory.presenter.ItemActivityPresenter
import com.example.android.inventory.presenter.ItemActivityPresenter.View
import com.example.android.inventory.repository.ItemRepository
import com.example.android.inventory.util.StringHelper
import com.example.android.inventory.util.getItem
import kotlinx.android.synthetic.main.activity_item.*

class ItemActivity : AppCompatActivity(), View {
    private var mIsActivityIsForEdit: Boolean = false

    private var mItem: Item = Item()
    private lateinit var mRepository: ItemRepository
    private lateinit var mPresenter: ItemActivityPresenter

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(StringHelper.IS_ACTIVITY_FOR_EDIT)) {
            mIsActivityIsForEdit = intent.getBooleanExtra(StringHelper.IS_ACTIVITY_FOR_EDIT, false)
        }

        mRepository = ItemRepository(application)
        mPresenter = ItemActivityPresenter(mItem, mRepository, this)

        supportActionBar?.let {
            it.title = getString(R.string.edit_Item)
        }
        if (mIsActivityIsForEdit) {
            mItem = intent.getItem()

            mPresenter = ItemActivityPresenter(mItem, mRepository, this)
            mPresenter.loadItemToTheActivity()

            supportActionBar?.let {
                it.subtitle = mItem.name
            }
        }

        btn_item_pic.setOnClickListener {
            takePicture()
        }
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Snackbar.make(cl_item, getString(R.string.cannot_take_picture), Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            val imageBitmap = extras?.get("data") as Bitmap

            setItemPicture(imageBitmap)

            mItem.picture = imageBitmap
        }
    }

    override fun onBackPressed() {
        mPresenter.confirmExitingWithoutSave()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        menu?.findItem(R.id.action_delete)?.isVisible = mIsActivityIsForEdit
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_delete -> {
                mPresenter.deleteItem()
                return true
            }
            R.id.action_save -> {
                save()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun save() = when {
        !TextUtils.isEmpty(ed_item_name.text.toString()) -> {
            when (mIsActivityIsForEdit) {
                true -> mPresenter.editItem()
                false -> mPresenter.addItem()
            }
            this@ItemActivity.finish()
        }
        else -> mPresenter.warningAboutRequiredItemName()
    }

    override fun setItemNameText(name: String) = ed_item_name.setText(name)

    override fun setItemTypeText(type: String?) = ed_item_type.setText(type)

    override fun setItemAmountText(amount: String?) = ed_item_amount.setText(amount)

    override fun setItemSupplierText(supplier: String?) = ed_item_supplier.setText(supplier)

    override fun setItemPicture(picture: Bitmap?) = iv_item_thumbnail.setImageBitmap(picture)

    override fun getItemNameText() = ed_item_name.text.toString()

    override fun getItemTypeText() = ed_item_type.text.toString()

    override fun getItemAmountText() = ed_item_amount.text.toString()

    override fun getItemSupplierText() = ed_item_supplier.text.toString()

    override fun getItemPicture(): Bitmap? {
        val itemImageDrawable = iv_item_thumbnail.drawable as BitmapDrawable
        return itemImageDrawable.bitmap
    }

    override fun showSnakeBar(message: String, length: Int) = Snackbar.make(cl_item, message, length).show()

    override fun showAlertDialogue(message: String, isCancellable: Boolean, positiveButtonClickListener: () -> Unit) {
        AlertDialog.Builder(this).apply {
            setMessage(message)
            setCancelable(isCancellable)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                positiveButtonClickListener()
                this@ItemActivity.finish()
            }
            setNegativeButton(getString(R.string.no)) { dialogue, _ ->
                dialogue.dismiss()
            }

        }.show()
    }
}
