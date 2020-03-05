package com.example.ecmmerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecmmerce.Model.Users;
import com.example.ecmmerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinnowbtn, loginbtn;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinnowbtn = findViewById(R.id.main_join_btn);
        loginbtn = findViewById(R.id.main_login_btn);
        progressDialog = new ProgressDialog(this);
        Paper.init(this);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        joinnowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });
        String phonekey = Paper.book().read(Prevalent.userphonekey);
        String passwordkey = Paper.book().read(Prevalent.userpasswordkey);
        if (passwordkey != "" && passwordkey != "") {
            if (!TextUtils.isEmpty(phonekey) && !TextUtils.isEmpty(passwordkey)) {
                AllowAccess(phonekey, passwordkey);
                progressDialog.setTitle("Already Login..");
                progressDialog.setMessage("Wait...........");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }

        }

    }

    private void AllowAccess(final String phone, final String password) {
        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()) {
                    Users userdata = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if (userdata.getPhone().equals(phone)) {
                        if (userdata.getPassword().equals(password)) {
                            Toast.makeText(MainActivity.this, "Done Logged In..!", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            progressDialog.dismiss();
                            //Toast.makeText(MainActivity.this, "Password Incorrect..!", Toast.LENGTH_LONG).show();
                        }

                    }


                } else {
                    Toast.makeText(MainActivity.this, "Account with " + phone + "Not Exist", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
