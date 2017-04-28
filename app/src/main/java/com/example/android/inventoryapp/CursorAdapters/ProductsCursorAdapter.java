package com.example.android.inventoryapp.CursorAdapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.DataBase.InventoryContract;
import com.example.android.inventoryapp.R;


public class ProductsCursorAdapter extends CursorAdapter {

    public ProductsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.product_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int productSupplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        int productImagePathColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE_PATH);

        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name);
        productNameTextView.setText(cursor.getString(productNameColumnIndex));

        TextView productPriceTextView = (TextView) view.findViewById(R.id.product_price);
        productPriceTextView.setText(String.valueOf(cursor.getInt(productPriceColumnIndex)) + "$");

        TextView productQuantityTextView = (TextView) view.findViewById(R.id.product_quantity);
        productQuantityTextView.setText("Quantity: " + String.valueOf(cursor.getInt(productQuantityColumnIndex)));

        TextView SupplierNameTextView = (TextView) view.findViewById(R.id.product_supplier);
        SupplierNameTextView.setText("Supplier: " + cursor.getString(productSupplierNameColumnIndex));

        ImageView productImageImageView = (ImageView) view.findViewById(R.id.product_image);
        String productImagePath = cursor.getString(productImagePathColumnIndex);
        if (productImagePath != null || !TextUtils.isEmpty(productImagePath)) {
            productImageImageView.setImageBitmap(BitmapFactory.decodeFile(productImagePath));
        }
    }
}
