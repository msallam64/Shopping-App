package com.example.ecmmerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecmmerce.R;
import com.example.ecmmerce.Sellers.SellerProductCategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductActivity extends AppCompatActivity {
    private Button applyChangesbtn, deleteBtn;
    private ImageView imageView;
    private EditText name, price, description;
    private String productID = "";
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);
        productID = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
        applyChangesbtn = findViewById(R.id.apply_changes_btn);
        imageView = findViewById(R.id.product_image_maintain);
        imageView = findViewById(R.id.product_image_maintain);
        name = findViewById(R.id.product_name_maintain);
        price = findViewById(R.id.product_price_maintain);
        description = findViewById(R.id.product_discribtion_maintain);
        deleteBtn = findViewById(R.id.delete_btn);
        displayProductInfo();
        applyChangesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThisProduct();
            }
        });


    }

    private void deleteThisProduct() {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(AdminMaintainProductActivity.this, SellerProductCategoryActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(AdminMaintainProductActivity.this, "Done ! ,Deleted ..", Toast.LENGTH_LONG).show();


            }
        });
    }

    private void applyChanges() {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDescription = description.getText().toString();
        if (pName.equals("")) {
            Toast.makeText(AdminMaintainProductActivity.this, "Write Product Name", Toast.LENGTH_LONG).show();
        } else if (pPrice.equals("")) {
            Toast.makeText(AdminMaintainProductActivity.this, "Write Product Price", Toast.LENGTH_LONG).show();

        } else if (pDescription.equals("")) {
            Toast.makeText(AdminMaintainProductActivity.this, "Write Product Description", Toast.LENGTH_LONG).show();
        } else {
            HashMap<String, Object> productmap = new HashMap<>();
            productmap.put("pid", productID);
            productmap.put("description", pDescription);
            productmap.put("price", pPrice);
            productmap.put("pname", pName);
            productsRef.updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminMaintainProductActivity.this, "Changes Applyed ,Done !", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AdminMaintainProductActivity.this, SellerProductCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displayProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String pName = dataSnapshot.child("pname").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String PDescription = dataSnapshot.child("description").getValue().toString();
                    String pImage = dataSnapshot.child("image").getValue().toString();
                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pPrice);
                    Picasso.get().load(pImage).into(imageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
