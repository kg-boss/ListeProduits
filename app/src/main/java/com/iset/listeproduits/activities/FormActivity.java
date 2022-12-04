package com.iset.listeproduits.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.iset.listeproduits.R;
import com.iset.listeproduits.models.Product;

import java.util.Objects;

public class FormActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 201;

    private TextInputEditText label, barCode, price;
    private SwitchCompat available;
    private Button camera, gallery, finish;
    private Bitmap image;
    private ImageView imageView;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        TextInputLayout barCodeLayout = findViewById(R.id.layout_barcode);
        barCodeLayout.setEndIconOnClickListener(onScan);

        label     = findViewById(R.id.input_label);
        barCode   = findViewById(R.id.input_barcode);
        price     = findViewById(R.id.input_price);
        available = findViewById(R.id.checkbox_available);

        available.setOnCheckedChangeListener(onAvailable);

        imageView = findViewById(R.id.image);

        camera  = findViewById(R.id.button_camera);
        gallery = findViewById(R.id.button_gallery);
        finish  = findViewById(R.id.button_finish);

        camera.setOnClickListener(onCamera);
        gallery.setOnClickListener(onGallery);
        finish.setOnClickListener(onFinish);

        if (getIntent().hasExtra("PRODUCT")) {
            product = (Product) getIntent().getSerializableExtra("PRODUCT");
            label.setText(product.getLabel());
            barCode.setText(product.getBarCode());
            price.setText(String.valueOf(product.getPrice()));
            available.setChecked(product.isAvailable());
            image = product.getImageBitmap();
            imageView.setImageBitmap(image);
            toolbar.setTitle(R.string.form_activity_title_update);
        }
    }


    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK) {
            if (data != null) {
                image = null;
                try { image = (Bitmap) data.getExtras().get("data"); } catch (Exception e) { e.printStackTrace(); }
                if (image != null)
                    imageView.setImageBitmap(image);

            }
        }
    });

    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK) {
            if (data != null) {
                image = null;
                try { image = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData())); } catch (Exception e) { e.printStackTrace(); }
                if (image != null)
                    imageView.setImageBitmap(image);
            }
        }
    });

    ActivityResultLauncher<Intent> scannerActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK) {
            if (data != null) {
                String barcode = null;
                try { barcode = data.getStringExtra("BAR_CODE"); } catch (Exception e) { e.printStackTrace(); }
                if (barcode != null)
                    barCode.setText(barcode);
            }
        }
    });

    private final View.OnClickListener onCamera = view -> {
        if (ContextCompat.checkSelfPermission(FormActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FormActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            return;
        }
        cameraActivityResultLauncher.launch(new Intent("android.media.action.IMAGE_CAPTURE"));
    };

    private final View.OnClickListener onGallery = view -> {
        if (ContextCompat.checkSelfPermission(FormActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FormActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
            return;
        }

        galleryActivityResultLauncher.launch(
                Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), "Select Image")
                        .putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {
                                new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*")
                        })
        );
    };

    private final View.OnClickListener onScan = view -> scannerActivityResultLauncher.launch(new Intent(FormActivity.this, ScanActivity.class));

    private final View.OnClickListener onFinish = view -> {
        setResult(RESULT_OK, new Intent().putExtra("PRODUCT", serializeProduct()));
        finish();
    };

    private final CompoundButton.OnCheckedChangeListener onAvailable = (view, checked) -> {
        if (checked) {
            available.setText(getString(R.string.form_switch_disponible));
            available.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            available.setText(getString(R.string.form_switch_indisponible));
            available.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    };

    @SuppressWarnings("")
    private Product serializeProduct() {
        String label = "", barCode = "";
        double price = 0;
        boolean available = true;

        try { label = this.label.getText().toString(); } catch (Exception e) { e.printStackTrace(); }
        try { barCode = this.barCode.getText().toString(); } catch (Exception e) { e.printStackTrace(); }
        try { price = Double.parseDouble(this.price.getText().toString()); } catch (Exception e) { e.printStackTrace(); }
        try { available = this.available.isChecked(); } catch (Exception e) { e.printStackTrace(); }

        return new Product(product == null ? 0 : product.getID(), label, barCode, price, available, image);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                onCamera.onClick(camera);
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                onGallery.onClick(gallery);

    }
}