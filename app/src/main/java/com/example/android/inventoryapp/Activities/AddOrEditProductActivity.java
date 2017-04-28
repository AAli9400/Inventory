package com.example.android.inventoryapp.Activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryapp.DataBase.InventoryContract;
import com.example.android.inventoryapp.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddOrEditProductActivity extends AppCompatActivity {

    Boolean isEditActivity;
    Boolean hasPicture;
    Uri ExistingUri;
    String mCurrentProductImagePath;

    EditText productNameEditText;
    EditText productPriceEditText;
    EditText productQuantityEditText;
    Spinner productSupplierSpinner;
    ImageView productImageImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_product);

        productNameEditText = (EditText) findViewById(R.id.product_name_edit_text);
        productPriceEditText = (EditText) findViewById(R.id.product_price_edit_text);
        productQuantityEditText = (EditText) findViewById(R.id.product_quantity_edit_text);

        productSupplierSpinner = (Spinner) findViewById(R.id.product_supplier_Spinner);
        ArrayList<String> suppliers = new ArrayList<>();
        String[] projection = {
                InventoryContract.SupplierEntry._ID,
                InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.SupplierEntry.COLUMN_SUPPLIER_PHONE,
                InventoryContract.SupplierEntry.COLUMN_SUPPLIER_EMAIL
        };
        Cursor cursor = getContentResolver().query(InventoryContract.SupplierEntry.CONTENT_URI, projection, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME);
            suppliers.add(cursor.getString(supplierNameColumnIndex));
        }
        if (cursor != null) cursor.close();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suppliers);
        productSupplierSpinner.setAdapter(spinnerAdapter);

        productImageImageView = (ImageView) findViewById(R.id.product_image_new);
        Button takeProductImageButton = (Button) findViewById(R.id.product_picture_button);

        mCurrentProductImagePath = "";
        hasPicture = false;
        /* Taking a full size image and store it on the device not in the database*/
        takeProductImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + "_";
                        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        photoFile = File.createTempFile(
                                imageFileName,  /* prefix */
                                ".jpg",         /* suffix */
                                storageDir      /* directory */
                        );

                    } catch (IOException ex) {
                        Toast.makeText(AddOrEditProductActivity.this, "Error Taking a photo", Toast.LENGTH_SHORT).show();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {

                        Uri photoURI = FileProvider.getUriForFile(AddOrEditProductActivity.this,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                        // manual granting the UriPermissions.
                        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }

                        //Start the camera to take an image of the product
                        startActivityForResult(takePictureIntent, 1);

                        //Delete the existed product image if any
                        if (hasPicture || (mCurrentProductImagePath != null && !TextUtils.isEmpty(mCurrentProductImagePath))) {
                            if (!new File(mCurrentProductImagePath).delete()) {
                                Toast.makeText(AddOrEditProductActivity.this, "Error Deleting the old image", Toast.LENGTH_SHORT).show();
                            }
                        }
                        mCurrentProductImagePath = photoFile.getAbsolutePath();
                    }
                }
            }
        });

        ExistingUri = getIntent().getData();
        if (ExistingUri != null) {
            isEditActivity = true;
            prepareActivityForEditing();
        } else isEditActivity = false;
    }

    /**
     * When user take a photo, show it on the product_image_new image view
     * (this activity before saving the new product)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            productImageImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentProductImagePath));
            hasPicture = true;
        }
    }

    private void prepareActivityForEditing() {
        //Changing the activity title
        setTitle("Edit Product");

        Cursor cursor = getContentResolver().query(ExistingUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int productSupplierColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int productImagePathColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE_PATH);

            productNameEditText.setText(cursor.getString(productNameColumnIndex));
            productPriceEditText.setText(String.valueOf(cursor.getInt(productPriceColumnIndex)));
            productQuantityEditText.setText(String.valueOf(cursor.getInt(productQuantityColumnIndex)));

            //Using (id of the supplier - 1) as an index here
            productSupplierSpinner.setSelection(cursor.getInt(productSupplierColumnIndex) - 1);

            mCurrentProductImagePath = cursor.getString(productImagePathColumnIndex);
            if (mCurrentProductImagePath.length() > 0) {
                productImageImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentProductImagePath));
                hasPicture = true;
            }

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
                saveProduct();
                NavUtils.navigateUpFromSameTask(AddOrEditProductActivity.this);
                return true;
            case android.R.id.home:
                confirmExiting();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        confirmExiting();
    }

    private void saveProduct() {
        String productName = productNameEditText.getText().toString().trim();
        String productPrice = productPriceEditText.getText().toString().trim();
        String productQuantity = productQuantityEditText.getText().toString().trim();
        String productSupplierName = productSupplierSpinner.getSelectedItem().toString();
        String productImagePath = mCurrentProductImagePath;

        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPrice) || TextUtils.isEmpty(productQuantity)) {
            Toast.makeText(this, "Please complete the product details", Toast.LENGTH_LONG).show();
        } else {
            ContentValues values = new ContentValues();
            values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
            values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE, Integer.parseInt(productPrice));
            values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(productQuantity));
            values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, productSupplierName);
            if (hasPicture)
                values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE_PATH, productImagePath);

            if (isEditActivity) {
                int rowUpdated = getContentResolver().update(ExistingUri, values, null, null);
                if (rowUpdated == 1) {
                    Toast.makeText(this, "Product Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error Updating the product", Toast.LENGTH_SHORT).show();
                }
            } else {
                Uri newRowUri = getContentResolver().insert(InventoryContract.ProductEntry.CONTENT_URI, values);
                if (newRowUri != null) {
                    Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error Adding the product", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void confirmExiting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Go back Without saving ?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (hasPicture) new File(mCurrentProductImagePath).delete();
                        NavUtils.navigateUpFromSameTask(AddOrEditProductActivity.this);
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
