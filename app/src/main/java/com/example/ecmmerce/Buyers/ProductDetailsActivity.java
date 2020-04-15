package com.example.ecmmerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecmmerce.Model.Product;
import com.example.ecmmerce.Prevalent.Prevalent;
import com.example.ecmmerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView productImage;
    private ElegantNumberButton numberBtn;
    private TextView productPrice, productDescription, productName;
    private String productID = "", state = "Normal";
    private Button addToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        productID = getIntent().getStringExtra("pid");

        addToCartButton = findViewById(R.id.pd_add_to_cart_btn);
        productImage = findViewById(R.id.product_image_details);
        numberBtn = findViewById(R.id.number_btn);
        productDescription = findViewById(R.id.product_description_details);
        productName = findViewById(R.id.product_name_details);
        productPrice = findViewById(R.id.product_price_details);
        getProductDetails(productID);

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (state.equals("Order Placed") || state.equals("Order Placed")) {
                    Toast.makeText(ProductDetailsActivity.this,
                            "You Can Make More Order When your order shipped or confirmed", Toast.LENGTH_LONG).show();

                }else {
                    addTocartList();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();
    }

    private void addTocartList() {
        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM DD, YYYY");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberBtn.getNumber());
        cartMap.put("discount", "");

        cartListRef.child("User View").child(Prevalent.currentonlineusers.getPhone()).child("Products")
                .child(productID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    cartListRef.child("Admin View").child(Prevalent.currentonlineusers.getPhone()).child("Products")
                            .child(productID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProductDetailsActivity.this, "Added To Cart", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });


                }
            }
        });


    }

    private void getProductDetails(String productID) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Product products = dataSnapshot.getValue(Product.class);
                    productName.setText(products.getPname());
                    productDescription.setText(products.getDescription());
                    productPrice.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(productImage);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkOrderState() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentonlineusers.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String shipedState = dataSnapshot.child("state").getValue().toString();
                    if (shipedState.equals("shiped")) {
                        state = "Order Shipped";

                    } else if (shipedState.equals("not shiped")) {
                        state = "Order Placed";


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
