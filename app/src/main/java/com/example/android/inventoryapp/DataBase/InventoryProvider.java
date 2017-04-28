package com.example.android.inventoryapp.DataBase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.io.File;

public class InventoryProvider extends ContentProvider {

    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;

    private static final int SUPPLIER = 102;
    private static final int SUPPLIER_ID = 103;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCT, PRODUCT);
        URI_MATCHER.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCT + "/#", PRODUCT_ID);

        URI_MATCHER.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_SUPPLIER, SUPPLIER);
        URI_MATCHER.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_SUPPLIER + "/#", SUPPLIER_ID);
    }

    private InventoryDbHelper inventoryDbHelper;

    @Override
    public boolean onCreate() {
        inventoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sqLiteDatabase = inventoryDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = URI_MATCHER.match(uri);
        switch (match) {
            case PRODUCT:
                cursor = sqLiteDatabase.query
                        (InventoryContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = InventoryContract.ProductEntry._ID + "=?";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = sqLiteDatabase.query
                        (InventoryContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SUPPLIER:
                cursor = sqLiteDatabase.query
                        (InventoryContract.SupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SUPPLIER_ID:
                selection = InventoryContract.ProductEntry._ID + "=?";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = sqLiteDatabase.query
                        (InventoryContract.SupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Register to watch the uri for changes.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqLiteDatabase = inventoryDbHelper.getWritableDatabase();

        int match = URI_MATCHER.match(uri);
        long id;
        switch (match) {
            case PRODUCT:
                id = sqLiteDatabase.insert(InventoryContract.ProductEntry.TABLE_NAME, null, values);
                if (id == -1) return null;
                else {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return ContentUris.withAppendedId(uri, id);
                }
            case SUPPLIER:
                id = sqLiteDatabase.insert(InventoryContract.SupplierEntry.TABLE_NAME, null, values);
                if (id == -1) return null;
                else {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return ContentUris.withAppendedId(uri, id);
                }
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = inventoryDbHelper.getWritableDatabase();

        int match = URI_MATCHER.match(uri);
        int rowUpdated = 0;
        switch (match) {
            case PRODUCT:
                rowUpdated = sqLiteDatabase.update(InventoryContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = InventoryContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowUpdated = sqLiteDatabase.update(InventoryContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SUPPLIER:
                rowUpdated = sqLiteDatabase.update(InventoryContract.SupplierEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SUPPLIER_ID:
                selection = InventoryContract.SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowUpdated = sqLiteDatabase.update(InventoryContract.SupplierEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        if (rowUpdated != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = inventoryDbHelper.getWritableDatabase();

        String[] projection = {
                InventoryContract.ProductEntry._ID,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE_PATH
        };
        Cursor cursor = null;

        int match = URI_MATCHER.match(uri);
        int rowDeleted = 0;
        switch (match) {
            case PRODUCT:
                cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, null);
                rowDeleted = sqLiteDatabase.delete(InventoryContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = InventoryContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, null);
                rowDeleted = sqLiteDatabase.delete(InventoryContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SUPPLIER:
                rowDeleted = sqLiteDatabase.delete(InventoryContract.SupplierEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SUPPLIER_ID:
                selection = InventoryContract.SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowDeleted = sqLiteDatabase.delete(InventoryContract.SupplierEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

            if (match == PRODUCT || match == PRODUCT_ID) {
                int productImagePathColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE_PATH);
                while (cursor != null && cursor.moveToNext()) {
                    new File(cursor.getString(productImagePathColumnIndex)).delete();
                }
                if (cursor != null) cursor.close();
            }
        }
        return rowDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // Getting the uri type;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case PRODUCT:
                return InventoryContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return InventoryContract.ProductEntry.CONTENT_ITEM_TYPE;
            case SUPPLIER:
                return InventoryContract.SupplierEntry.CONTENT_LIST_TYPE;
            case SUPPLIER_ID:
                return InventoryContract.SupplierEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
