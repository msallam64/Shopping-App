package com.example.ecmmerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ecmmerce.R;
import com.example.ecmmerce.interFaces.itemClickListner;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productNameTV, productDiscriptionTV, productPrice, productState;
    public ImageView imageView;
    public itemClickListner listner;

    public ItemViewHolder(View itemView) {
        super(itemView);
        productNameTV = itemView.findViewById(R.id.product_seller_name);
        productDiscriptionTV = itemView.findViewById(R.id.product_seller_discribtion);
        imageView = itemView.findViewById(R.id.product_seller_image);
        productState = itemView.findViewById(R.id.product_seller_state);
        productPrice = itemView.findViewById(R.id.product_seller_price);

    }

    public void setItemClickListner(itemClickListner listner) {
        this.listner = listner;

    }

    @Override
    public void onClick(View v) {
        listner.onClick(v, getAdapterPosition(), false);

    }
}

