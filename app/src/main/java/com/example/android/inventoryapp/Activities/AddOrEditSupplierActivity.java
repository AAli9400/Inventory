package com.example.android.inventoryapp.Activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.DataBase.InventoryContract;
import com.example.android.inventoryapp.R;

public class AddOrEditSupplierActivity extends AppCompatActivity {

    Boolean isEditActivity;
    Uri ExistingUri;

    EditText supplierNameEditText;
    EditText supplierPhoneEditText;
    EditText supplierEmailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_supplier);

        supplierNameEditText = (EditText) findViewById(R.id.supplier_name_edit_text);
        supplierPhoneEditText = (EditText) findViewById(R.id.supplier_phone_edit_text);
        supplierEmailEditText = (EditText) findViewById(R.id.supplier_email_edit_text);

        ExistingUri = getIntent().getData();
        if (ExistingUri != null) {
            isEditActivity = true;
            prepareActivityForEditing();
        } else isEditActivity = false;
    }

    private void prepareActivityForEditing() {
        //Changing the activity title
        setTitle("Edit Supplier");

        Cursor cursor = getContentResolver().query(ExistingUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_PHONE);
            int supplierEmailColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_EMAIL);

            supplierNameEditText.setText(cursor.getString(supplierNameColumnIndex));
            supplierPhoneEditText.setText(cursor.getString(supplierPhoneColumnIndex));
            supplierEmailEditText.setText(cursor.getString(supplierEmailColumnIndex));

            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.meun_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                saveSupplier();
                NavUtils.navigateUpFromSameTask(AddOrEditSupplierActivity.this);
                return true;
            case android.R.id.home:
                confirmExiting();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveSupplier() {
        String supplierName = supplierNameEditText.getText().toString();
        String supplierPhone = supplierPhoneEditText.getText().toString();
        String supplierEmail = supplierEmailEditText.getText().toString();

        if(TextUtils.isEmpty(supplierName) || TextUtils.isEmpty(supplierPhone) || TextUtils.isEmpty(supplierEmail)){
            Toast.makeText(this, "Please complete the supplier details", Toast.LENGTH_LONG).show();
        }
        else {
            ContentValues values = new ContentValues();
            values.put(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME, supplierName);
            values.put(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);
            values.put(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);

            if (isEditActivity) {
                int rowUpdated = getContentResolver().update(ExistingUri, values, null, null);
                if (rowUpdated == 1) {
                    Toast.makeText(this, "Supplier Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error Updating the supplier", Toast.LENGTH_SHORT).show();
                }
            } else {
                Uri newRowUri = getContentResolver().insert(InventoryContract.SupplierEntry.CONTENT_URI, values);
                if (newRowUri != null) {
                    Toast.makeText(this, "Supplier Added Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error Adding the supplier", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        confirmExiting();
    }

    private void confirmExiting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Go back Without saving ?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(AddOrEditSupplierActivity.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
