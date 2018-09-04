package com.example.android.inventory.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.android.inventory.R
import com.example.android.inventory.model.Item
import kotlinx.android.synthetic.main.items_list_item.view.*

class ItemsRecyclerViewAdapter(
        private var mItems: MutableList<Item>? = null,
        private val mListener: ListInteractionListener,
        private val mResources: RecyclerViewResources)
    : RecyclerView.Adapter<ItemsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.items_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mItems?.get(position)
        var text: String?

        item?.let {
            holder.apply {

                it.picture?.let { picture ->
                    //val pic = BitmapFactory.decodeByteArray(picture, 0, picture.size)
                    mItemImage.setImageBitmap(picture)
                }

                mItemName.text = it.name

                text = when {
                    !it.type.isNullOrBlank() ->
                        mResources.getString(R.string.type) + it.type
                    else -> mResources.getString(R.string.type_unknown)
                }
                mItemType.text = text

                text = when {
                    !it.amount.isNullOrBlank() -> mResources.getString(R.string.amount) + it.amount
                    else -> mResources.getString(R.string.amount_unknown)
                }
                mItemAmount.text = text

                text = when {
                    !it.supplier.isNullOrBlank() -> mResources.getString(R.string.supplier) + it.type
                    else -> mResources.getString(R.string.supplier_unknown)
                }
                mItemSupplier.text = text

                mView.setOnClickListener { _ ->
                    mListener.onRecyclerViewItemClickListener(it)
                }
            }
        }
    }

    fun setItems(items: List<Item>?) {
        mItems = items?.toMutableList()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Item? {
        return mItems?.get(position)
    }

    fun removeItem(position: Int): Item? {
        mItems?.let {
            val itemRemoved = it.removeAt(position)

            notifyItemRemoved(position)

            return itemRemoved
        }
        return null
    }

    fun addItem(item: Item?, position: Int) {
        mItems?.let { mItems ->
            item?.let { item ->
                mItems.add(position, item)
                notifyItemInserted(position)
            }
        }
    }

    fun removeAll() {
        mItems?.let {
            it.clear()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        mItems?.let { return it.size }
        return 0
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mItemImage: ImageView = mView.iv_item_image
        val mItemName: TextView = mView.tv_item_name
        val mItemSupplier: TextView = mView.tv_item_supplier
        val mItemAmount: TextView = mView.tv_item_amount
        val mItemType: TextView = mView.tv_type
    }

    interface ListInteractionListener {
        fun onRecyclerViewItemClickListener(item: Item)
    }

    interface RecyclerViewResources {
        fun getString(id: Int): String
    }
}
