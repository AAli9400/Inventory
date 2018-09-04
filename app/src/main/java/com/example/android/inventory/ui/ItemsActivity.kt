package com.example.android.inventory.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import com.example.android.inventory.R
import com.example.android.inventory.model.Item
import com.example.android.inventory.presenter.ItemsActivityPresenter
import com.example.android.inventory.presenter.ItemsActivityPresenter.View
import com.example.android.inventory.repository.ItemsViewModel
import com.example.android.inventory.ui.ItemsRecyclerViewAdapter.ListInteractionListener
import com.example.android.inventory.ui.ItemsRecyclerViewAdapter.RecyclerViewResources
import com.example.android.inventory.util.StringHelper
import com.example.android.inventory.util.setItem
import kotlinx.android.synthetic.main.activity_items.*

class ItemsActivity : AppCompatActivity(), ListInteractionListener, RecyclerViewResources, View {
    private lateinit var mViewModel: ItemsViewModel
    private lateinit var mItemRecyclerViewAdapter: ItemsRecyclerViewAdapter
    private lateinit var mPresenter: ItemsActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)


        mViewModel = ViewModelProviders.of(this).get(ItemsViewModel::class.java)

        mItemRecyclerViewAdapter = ItemsRecyclerViewAdapter(
                mViewModel.getAllItems()?.value?.toMutableList(),
                this,
                this
        )

        mPresenter = ItemsActivityPresenter(
                mRecyclerViewAdapter = mItemRecyclerViewAdapter,
                mViewModel = mViewModel,
                mView = this
        )

        rv_items_recycler_view.adapter = mItemRecyclerViewAdapter
        rv_items_recycler_view.layoutManager = LinearLayoutManager(this)

        val swipeToDeleteCallback = mPresenter.createSwipeToDeleteCallback()
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(rv_items_recycler_view)

        mViewModel.getAllItems()?.observe(this, Observer { items ->
            mItemRecyclerViewAdapter.setItems(items)
        })

        fab_add_item.setOnClickListener {
            val intent = Intent(this@ItemsActivity, ItemActivity::class.java)
            intent.putExtra(StringHelper.IS_ACTIVITY_FOR_EDIT, false)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)

        val deleteAllMenuItem = menu?.findItem(R.id.action_delete_all)

        mViewModel.getAllItems()?.observe(this, Observer { items ->
            items?.isNotEmpty()?.let { deleteAllMenuItem?.setEnabled(it) }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_delete_all) {
            mPresenter.confirmDeletion()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRecyclerViewItemClickListener(item: Item) {
        val intent = Intent(this@ItemsActivity, ItemActivity::class.java)
        intent.setItem(item)
        intent.putExtra(StringHelper.IS_ACTIVITY_FOR_EDIT, true)
        startActivity(intent)
    }

    override fun showAlertDialogue(message: String, isCancellable: Boolean, positiveButtonClickListener: () -> Unit) {
        AlertDialog.Builder(this).apply {
            setMessage(message)
            setCancelable(isCancellable)
            setPositiveButton(getString(R.string.yes)) { _, _ -> positiveButtonClickListener() }
            setNegativeButton(getString(R.string.no)) { dialogue, _ -> dialogue.dismiss() }
        }.show()
    }

    override fun showSnakeBar(text: String, length: Int, callbackOnDismissed: () -> Unit, actionText: String, actionClickListener: () -> Unit) {
        val callback = SnakeBarCallback(callbackOnDismissed)
        Snackbar.make(cl_items, text, length).apply {
            addCallback(callback)
            setAction(actionText) {
                this@apply.removeCallback(callback)
                actionClickListener()
            }
        }.show()
    }
}
