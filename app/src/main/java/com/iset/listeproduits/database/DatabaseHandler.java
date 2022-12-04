package com.iset.listeproduits.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iset.listeproduits.models.Product;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "productsManager";
    private static final String TABLE_PRODUCTS = "products";
    private static final String KEY_ID = "id";
    private static final String KEY_LABEL = "label";
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_PRICE = "price";
    private static final String KEY_AVAILABLE = "available";
    private static final String KEY_IMAGE = "image";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_LABEL + " TEXT,"
                + KEY_BARCODE + " TEXT,"
                + KEY_PRICE + " REAL,"
                + KEY_AVAILABLE + " NUMERIC,"
                + KEY_IMAGE + " BLOB"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = writeProduct(product);

        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    public Product getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUCTS, new String[] { KEY_ID, KEY_LABEL, KEY_BARCODE,
                        KEY_PRICE, KEY_AVAILABLE, KEY_IMAGE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor == null)
            return null;
        if (cursor.moveToFirst())
            return readProduct(cursor);
        return null;
    }

    // code to get all contacts in a list view
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do
                productList.add(readProduct(cursor));
            while (cursor.moveToNext());
        }

        // return contact list
        return productList;
    }

    // code to update the single contact
    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = writeProduct(product);

        return db.update(TABLE_PRODUCTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(product.getID()) });
    }

    // Deleting single contact
    public void deleteProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, KEY_ID + " = ?",
                new String[] { String.valueOf(product.getID()) });
        db.close();
    }

    // Getting contacts Count
    public int getProductsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    private ContentValues writeProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put(KEY_LABEL, product.getLabel());
        values.put(KEY_BARCODE, product.getBarCode());
        values.put(KEY_PRICE, product.getPrice());
        values.put(KEY_AVAILABLE, product.isAvailable());
        values.put(KEY_IMAGE, product.getImage());
        return values;
    }

    private Product readProduct(Cursor cursor) {
        Product product = new Product();
        product.setID(Integer.parseInt(cursor.getString(0)));
        product.setLabel(cursor.getString(1));
        product.setBarCode(cursor.getString(2));
        product.setPrice(cursor.getDouble(3));
        product.setAvailable(cursor.getInt(4) != 0);
        product.setImage(cursor.getBlob(5));
        return product;
    }

}
