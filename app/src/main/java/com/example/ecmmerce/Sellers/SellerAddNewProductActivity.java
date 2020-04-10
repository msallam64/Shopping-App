package com.example.ecmmerce.Sellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecmmerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellerAddNewProductActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private String categoryname, discription, price, pname,
            savecurrentdate, savecurrenttime;
    private Button addNewProduct;
    private EditText productName, productDescrption, productPrice;
    private ImageView productimage;
    private static final int Gallerypick = 1;
    private Uri imageuri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productimagesref;
    private DatabaseReference productRef, sellersRef;
    private String sName, sAddress, sPhone, sEmail, sId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_new_product);
        progressDialog = new ProgressDialog(this);
        categoryname = getIntent().getExtras().get("category").toString().trim();
        productimagesref = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        sellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");


        addNewProduct = findViewById(R.id.add_new_product);
        productimage = findViewById(R.id.select_image_product);
        productName = findViewById(R.id.product_name);
        productDescrption = findViewById(R.id.product_discrption);
        productPrice = findViewById(R.id.product_price);
        productimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        addNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valdateProductData();
            }
        });
        sellersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            sName = dataSnapshot.child("name").getValue().toString();
                            sEmail = dataSnapshot.child("email").getValue().toString();
                            sPhone = dataSnapshot.child("phone").getValue().toString();
                            sAddress = dataSnapshot.child("address").getValue().toString();
                            sId = dataSnapshot.child("sid").getValue().toString();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void valdateProductData() {
        discription = productDescrption.getText().toString();
        price = productPrice.getText().toString();
        pname = productName.getText().toString();
        if (imageuri == null) {
            Toast.makeText(this, "Select Product Image....", Toast.LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(discription)) {
            Toast.makeText(this, "Write Product Discription...", Toast.LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Write Product Price...", Toast.LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(pname)) {
            Toast.makeText(this, "Write Product Name...", Toast.LENGTH_LONG).show();

        } else {
            storeProductInformation();
        }

    }

    private void storeProductInformation() {
        progressDialog.setTitle("Adding New Product..");
        progressDialog.setMessage("Wait...........");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd, yyyy");
        savecurrentdate = currentdate.format(calendar.getTime());

        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss a");
        savecurrenttime = currenttime.format(calendar.getTime());

        productRandomKey = savecurrentdate + savecurrenttime;

        final StorageReference filepath = productimagesref.child(imageuri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filepath.putFile(imageuri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String Error = e.toString();
                Toast.makeText(SellerAddNewProductActivity.this, "Error : " + Error, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SellerAddNewProductActivity.this, "Image Upload Successful..", Toast.LENGTH_LONG).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        downloadImageUrl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(SellerAddNewProductActivity.this,
                                    "Image URL Save To DataBase Successful...", Toast.LENGTH_SHORT).show();
                            saveProductInfoToDataBase();
                        }
                    }
                });


            }
        });
    }

    private void saveProductInfoToDataBase() {
        HashMap<String, Object> productmap = new HashMap<>();
        productmap.put("pid", productRandomKey);
        productmap.put("date", savecurrentdate);
        productmap.put("time", savecurrenttime);
        productmap.put("description", discription);
        productmap.put("image", downloadImageUrl);
        productmap.put("catagory", categoryname);
        productmap.put("price", price);
        productmap.put("pname", pname);

        productmap.put("sellername", sName);
        productmap.put("selleraddress", sAddress);
        productmap.put("sellerphone", sPhone);
        productmap.put("selleremail", sEmail);
        productmap.put("sellerid", sId);
        productmap.put("productstate", "not approved");

        productRef.child(productRandomKey).updateChildren(productmap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SellerAddNewProductActivity.this, SellerHomeActivity.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            Toast.makeText(SellerAddNewProductActivity.this, "Product is Add Success...", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            String Error = task.getException().toString();
                            Toast.makeText(SellerAddNewProductActivity.this, "Error : " + Error, Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallerypick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallerypick && resultCode == RESULT_OK && data != null) {
            imageuri = data.getData();
            productimage.setImageURI(imageuri);
        }
    }
}
