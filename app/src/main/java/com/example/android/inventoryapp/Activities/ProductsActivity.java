package com.example.android.inventoryapp.Activities;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.CursorAdapters.ProductsCursorAdapter;
import com.example.android.inventoryapp.DataBase.InventoryContract;
import com.example.android.inventoryapp.R;

public class ProductsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ProductsCursorAdapter productsCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Products");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = getContentResolver().query(InventoryContract.SupplierEntry.CONTENT_URI, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    Intent intent = new Intent(ProductsActivity.this, AddOrEditProductActivity.class);
                    startActivity(intent);
                    cursor.close();
                } else
                    Toast.makeText(ProductsActivity.this, "Please Add Supplier First", Toast.LENGTH_SHORT).show();

            }
        });

        ListView productListView = (ListView) findViewById(R.id.products_list_view);

        View productEmptyStateView = findViewById(R.id.product_empty_state_text_view);
        productListView.setEmptyView(productEmptyStateView);

        productsCursorAdapter = new ProductsCursorAdapter(this, null);
        productListView.setAdapter(productsCursorAdapter);

        registerForContextMenu(productListView);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_products:
                deleteAllProducts();
                return true;
            case R.id.action_suppliers:
                Intent suppliersIntent = new Intent(this, SuppliersActivity.class);
                startActivity(suppliersIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Load the context menu items from product_menu_context.
        getMenuInflater().inflate(R.menu.product_menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Getting selected item info to use it's id (info.id)
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_edit_product:
                Intent editProductIntent = new Intent(this, AddOrEditProductActivity.class);
                editProductIntent.setData(ContentUris.withAppendedId(InventoryContract.ProductEntry.CONTENT_URI, info.id));
                startActivity(editProductIntent);
                return true;
            case R.id.action_delete_product:
                deleteProduct(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteProduct(final long id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete This Product ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rowDeleted = getContentResolver().delete(ContentUris.withAppendedId(InventoryContract.ProductEntry.CONTENT_URI, id), String.valueOf(id), null);
                        if (rowDeleted == 1) {
                            Toast.makeText(ProductsActivity.this, "Product Deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductsActivity.this, "Error Deleting the Product", Toast.LENGTH_SHORT).show();
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

    private void deleteAllProducts() {
        Cursor cursor = getContentResolver().query(InventoryContract.ProductEntry.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Delete All Products ?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int rowDeleted = getContentResolver().delete(InventoryContract.ProductEntry.CONTENT_URI, null, null);
                            if (rowDeleted != 0) {
                                Toast.makeText(ProductsActivity.this, "All Products Deleted.", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(ProductsActivity.this, "Error deleting Products.", Toast.LENGTH_SHORT).show();

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
        } else Toast.makeText(this, "No Product To Delete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.ProductEntry._ID,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE_PATH
        };

        String sortOrder = InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME + " ASC";

        return new CursorLoader(
                this,
                InventoryContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        productsCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productsCursorAdapter.swapCursor(null);
    }
}
