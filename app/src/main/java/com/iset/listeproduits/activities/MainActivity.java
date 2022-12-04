package com.iset.listeproduits.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iset.listeproduits.R;
import com.iset.listeproduits.adapters.ProductAdapter;
import com.iset.listeproduits.database.DatabaseHandler;
import com.iset.listeproduits.models.Product;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHandler database;
    ProductAdapter adapter;

    ImageButton update, delete;
    FloatingActionButton create;
    TextView title;

    Product product = null;
    List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title);
        create = findViewById(R.id.button_create);
        update = findViewById(R.id.button_update);
        delete = findViewById(R.id.button_delete);

        delete.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Supprimer");
            alert.setMessage("Voulez vous vraiment supprimer les produits séléctionnés?\nCette action est irriversible.");
            alert.setPositiveButton("Supprimer", (dialogInterface, i) -> {
                for(Product p: productList)
                    database.deleteProduct(p);
                adapter.refresh(database.getAllProducts());
                dialogInterface.dismiss();
            });
            alert.setNegativeButton("Annuler", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            alert.show();
        });

        database = new DatabaseHandler(this);
        adapter = new ProductAdapter(this, database.getAllProducts());
        ListView list = findViewById(R.id.list); list.setAdapter(adapter);
        create.setOnClickListener(view -> createActivityResultLauncher.launch(new Intent(MainActivity.this, FormActivity.class)));
        update.setOnClickListener(view -> updateActivityResultLauncher.launch(new Intent(MainActivity.this, FormActivity.class).putExtra("PRODUCT", product)));

        adapter.setOnSelectionChangedListener(new ProductAdapter.OnSelectionChangedListener() {
            @Override
            public void onNothingSelected() {
                update.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                title.setText(getString(R.string.main_toolbar_label_normal));
            }

            @Override
            public void onProductSelected(Product p) {
                product = p;
                productList = new ArrayList<>(); productList.add(p);
                update.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                title.setText(getString(R.string.main_toolbar_label_select_single));
            }

            @Override
            public void onSelectMultiple(List<Product> products) {
                productList = products;
                update.setVisibility(View.GONE);
                delete.setVisibility(View.VISIBLE);
                title.setText(getString(R.string.main_toolbar_label_select_multiple).replace("%NUMBER%", String.valueOf(products.size())));
            }
        });
    }

    ActivityResultLauncher<Intent> createActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK) {
            if (data != null) {
                Product product = null;
                try { product = (Product) data.getSerializableExtra("PRODUCT"); } catch (Exception e) { e.printStackTrace(); }
                if (product != null) {
                    database.addProduct(product);
                    adapter.refresh(database.getAllProducts());
                }
            }
        }
    });

    ActivityResultLauncher<Intent> updateActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK) {
            if (data != null) {
                Product product = null;
                try { product = (Product) data.getSerializableExtra("PRODUCT"); } catch (Exception e) { e.printStackTrace(); }
                if (product != null) {
                    database.updateProduct(product);
                    adapter.refresh(database.getAllProducts());
                }
            }
        }
    });
}