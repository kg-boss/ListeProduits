package com.iset.listeproduits.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.iset.listeproduits.R;
import com.iset.listeproduits.models.Product;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private List<Product> list;
    private final Context context;

    private OnSelectionChangedListener listener = null;



    public ProductAdapter(@NotNull Context context, @NotNull List<Product> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.product_list_item, null);
        }

        Product product = list.get(i);

        TextView txtLabel = view.findViewById(R.id.text_label); txtLabel.setText(product.getLabel());
        TextView txtBarCode = view.findViewById(R.id.text_barcode); txtBarCode.setText(product.getBarCode());
        TextView txtPrice = view.findViewById(R.id.text_price); txtPrice.setText(product.getPriceString());
        ImageView image = view.findViewById(R.id.image); image.setImageBitmap(product.getImageBitmap());

        view.findViewById(R.id.text_available_on ).setVisibility(product.isAvailable() ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.text_available_off).setVisibility(product.isAvailable() ? View.GONE : View.VISIBLE);

        CheckBox checkBox = view.findViewById(R.id.checkBox);

        checkBox.setOnCheckedChangeListener((v, checked) -> {
            product.checked = checked;
            if (listener == null)
                return;
            List<Product> selection = getCheckedProducts();
            int length = selection.size();
            if (length == 0)
                listener.onNothingSelected();
            else if (length == 1)
                listener.onProductSelected(selection.get(0));
            else
                listener.onSelectMultiple(selection);

        });

        return view;
    }

    public void refresh(@NotNull List<Product> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }


    public List<Product> getCheckedProducts() {
        List<Product> list = new ArrayList<>();
        for (Product p: this.list)
            if (p.checked)
                list.add(p);
        return list;
    }

    public interface OnSelectionChangedListener {
        void onNothingSelected();
        void onProductSelected(Product product);
        void onSelectMultiple(List<Product> products);
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.listener = listener;
    }
}
