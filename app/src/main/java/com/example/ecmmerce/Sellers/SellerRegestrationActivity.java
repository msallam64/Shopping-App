package com.example.ecmmerce.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecmmerce.Buyers.MainActivity;
import com.example.ecmmerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerRegestrationActivity extends AppCompatActivity {
    private Button sellerLoginBegin;
    private EditText nameInput, phoneInput, emailInput, passwordInput, addressInput;
    private Button registerBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_regestration);

        mAuth = FirebaseAuth.getInstance();

        nameInput = findViewById(R.id.seller_name);
        phoneInput = findViewById(R.id.seller_phone);
        emailInput = findViewById(R.id.seller_email);
        passwordInput = findViewById(R.id.seller_password);
        addressInput = findViewById(R.id.seller_address);
        registerBtn = findViewById(R.id.seller_register_btn);

        sellerLoginBegin = findViewById(R.id.seller_have_account_btn);
        sellerLoginBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerRegestrationActivity.this, SellerLoginActivity.class);
                startActivity(intent);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSeller();
            }
        });
    }

    private void registerSeller() {
         final String name = nameInput.getText().toString();
         final String phone = phoneInput.getText().toString();
         final String email = emailInput.getText().toString();
         String password = passwordInput.getText().toString();
         final String address = addressInput.getText().toString();

        if (!name.equals("") && !phone.equals("") && !email.equals("") && !password.equals("") && !address.equals("")) {
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        final DatabaseReference rootRef ;
                        rootRef= FirebaseDatabase.getInstance().getReference();
                        String sid = mAuth.getCurrentUser().getUid();
                        HashMap<String, Object> sellerMap = new HashMap<>();
                        sellerMap.put("sid", sid);
                        sellerMap.put("phone", phone);
                        sellerMap.put("email", email);
                        sellerMap.put("address", address);
                        sellerMap.put("name", name);
                        rootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SellerRegestrationActivity.this, "Done ..", Toast.LENGTH_LONG).show();
                                        Intent intent=new Intent(SellerRegestrationActivity.this, SellerHomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                    }else {
                        Toast.makeText(SellerRegestrationActivity.this, "Faield ..", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Toast.makeText(SellerRegestrationActivity.this, "Please Complete Form ..", Toast.LENGTH_LONG).show();
        }
    }
}
