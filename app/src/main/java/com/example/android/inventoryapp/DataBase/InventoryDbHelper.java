package com.example.android.inventoryapp.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.DataBase.InventoryContract.ProductEntry;
import com.example.android.inventoryapp.DataBase.InventoryContract.SupplierEntry;


public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.dp";
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_IMAGE_PATH + " TEXT DEFAULT '')";
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);

        String SQL_CREATE_SUPPLIER_TABLE = "CREATE TABLE " + SupplierEntry.TABLE_NAME + "("
                + SupplierEntry._ID + " INTEGER PRIMARY KEY, "
                + SupplierEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + SupplierEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, "
                + SupplierEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL)";
        db.execSQL(SQL_CREATE_SUPPLIER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
