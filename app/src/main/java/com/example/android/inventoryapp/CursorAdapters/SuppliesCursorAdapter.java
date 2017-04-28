package com.example.android.inventoryapp.CursorAdapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.DataBase.InventoryContract;
import com.example.android.inventoryapp.R;

public class SuppliesCursorAdapter extends CursorAdapter {

    public SuppliesCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.supplier_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int supplierNameIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME);
        int supplierEmailIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_EMAIL);
        int supplierPhoneIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_PHONE);

        TextView supplierNameTextView = (TextView) view.findViewById(R.id.supplier_name);
        supplierNameTextView.setText(cursor.getString(supplierNameIndex));

        TextView supplierPhoneTextView = (TextView) view.findViewById(R.id.supplier_phone);
        supplierPhoneTextView.setText("Phone: " + cursor.getString(supplierPhoneIndex));

        TextView supplierEmailTextView = (TextView) view.findViewById(R.id.supplier_email);
        supplierEmailTextView.setText("Email: " + cursor.getString(supplierEmailIndex));
    }
}
