package com.example.android.inventoryapp.Activities;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.CursorAdapters.SuppliesCursorAdapter;
import com.example.android.inventoryapp.DataBase.InventoryContract;
import com.example.android.inventoryapp.R;

public class SuppliersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SuppliesCursorAdapter suppliesCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SuppliersActivity.this, AddOrEditSupplierActivity.class);
                startActivity(intent);
            }
        });


        ListView supplierListView = (ListView) findViewById(R.id.suppliers_list_view);

        View supplierEmptyStateView = findViewById(R.id.supplier_empty_state_text_view);
        supplierListView.setEmptyView(supplierEmptyStateView);

        suppliesCursorAdapter = new SuppliesCursorAdapter(this, null);
        supplierListView.setAdapter(suppliesCursorAdapter);

        registerForContextMenu(supplierListView);

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_supplier, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_suppliers:
                deleteAllSuppliers();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Load the context menu items from product_menu_context.
        getMenuInflater().inflate(R.menu.supplier_menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Getting selected item info to use it's id (info.id)
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.action_contact_Supplier:
                PopupMenu popupMenu = new PopupMenu(this, new View(this));
                popupMenu.inflate(R.menu.menu_contact_supplier);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_call:
                                callSupplier(info.id);
                                return true;
                            case R.id.action_text_message:
                                textMessageSupplier(info.id);
                                return true;
                            case R.id.action_Email:
                                mailSupplier(info.id);
                                return true;
                            default:
                                return true;
                        }
                    }
                });
                popupMenu.show();
                return true;
            case R.id.action_edit_supplier:
                Intent editSupplierIntent = new Intent(this, AddOrEditSupplierActivity.class);
                editSupplierIntent.setData(ContentUris.withAppendedId(InventoryContract.SupplierEntry.CONTENT_URI, info.id));
                startActivity(editSupplierIntent);
                return true;
            case R.id.action_delete_supplier:
                deleteSupplier(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void callSupplier(long id) {
        String[] projection = {
                InventoryContract.SupplierEntry._ID,
                InventoryContract.SupplierEntry.COLUMN_SUPPLIER_PHONE
        };
        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(InventoryContract.SupplierEntry.CONTENT_URI, id), projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_PHONE);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + supplierPhone));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            cursor.close();
        }
    }

    private void textMessageSupplier(long id) {
        String[] projection = {
                InventoryContract.SupplierEntry._ID,
                InventoryContract.SupplierEntry.COLUMN_SUPPLIER_PHONE
        };
        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(InventoryContract.SupplierEntry.CONTENT_URI, id), projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_PHONE);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + supplierPhone));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            cursor.close();
        }
    }

    private void mailSupplier(long id) {
        String[] projection = {
                InventoryContract.SupplierEntry._ID,
                InventoryContract.SupplierEntry.COLUMN_SUPPLIER_EMAIL
        };

        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(InventoryContract.SupplierEntry.CONTENT_URI, id), projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int supplierEmailColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_EMAIL);
            String supplierEmailAddress = cursor.getString(supplierEmailColumnIndex);

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + supplierEmailAddress));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            cursor.close();
        }
    }

    private void deleteSupplier(final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete This Supplier ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rowDeleted = getContentResolver().delete(ContentUris.withAppendedId(InventoryContract.SupplierEntry.CONTENT_URI, id), String.valueOf(id), null);
                        if (rowDeleted == 1) {
                            Toast.makeText(SuppliersActivity.this, "Supplier Deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SuppliersActivity.this, "Error deleting the Supplier.", Toast.LENGTH_SHORT).show();
                        }
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

    private void deleteAllSuppliers() {
        Cursor cursor = getContentResolver().query(InventoryContract.SupplierEntry.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Delete All Suppliers ?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int rowDeleted = getContentResolver().delete(InventoryContract.SupplierEntry.CONTENT_URI, null, null);
                            if (rowDeleted != 0) {
                                Toast.makeText(SuppliersActivity.this, "All Suppliers Deleted.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SuppliersActivity.this, "Error deleting Suppliers.", Toast.LENGTH_SHORT).show();
                            }
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
            cursor.close();
        } else Toast.makeText(this, "No Supplier To Delete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.SupplierEntry._ID,
                InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.SupplierEntry.COLUMN_SUPPLIER_PHONE,
                InventoryContract.SupplierEntry.COLUMN_SUPPLIER_EMAIL
        };

        String sortOrder = InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME + " ASC";
        return new CursorLoader(
                this,
                InventoryContract.SupplierEntry.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        suppliesCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        suppliesCursorAdapter.swapCursor(null);
    }
}
