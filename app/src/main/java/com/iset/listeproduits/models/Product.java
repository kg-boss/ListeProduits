package com.iset.listeproduits.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class Product implements Serializable {

    public boolean checked = false;

    private int id;
    private String label;
    private String barCode;
    private double price;
    private boolean available;
    private byte[] image;

    public Product() { }

    public Product(String label, String barCode, double price, boolean available, Bitmap image) {
        this(0, label, barCode, price, available, image);
    }
    public Product(int id, String label, String barCode, double price, boolean available, Bitmap image) {
        this.id = id;
        this.label = label;
        this.barCode = barCode;
        this.price = price;
        this.available = available;
        setImage(image);
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Bitmap getImageBitmap() {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public void setImage(Bitmap image) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (image != null)
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        this.image = bos.toByteArray();
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getPriceString() {
        return price + " TND";
    }
}
