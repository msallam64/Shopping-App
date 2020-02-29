package com.example.ecmmerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecmmerce.R;
import com.example.ecmmerce.interFaces.itemClickListner;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView productNameTV,productDiscriptionTV,productPrice;
    public ImageView imageView;
    public itemClickListner listner;
    public ProductViewHolder(View itemView) {
        super(itemView);
        productNameTV=itemView.findViewById(R.id.product_name);
        productDiscriptionTV=itemView.findViewById(R.id.product_discribtion);
        imageView=itemView.findViewById(R.id.product_image);
        productPrice=itemView.findViewById(R.id.product_price);

    }
    public void setItemClickListner(itemClickListner listner){
        this.listner=listner;

    }

    @Override
    public void onClick(View v) {
        listner.onClick(v,getAdapterPosition(),false);

    }
}
