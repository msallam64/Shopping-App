package com.example.ecmmerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecmmerce.Admin.AdminHomeActivity;
import com.example.ecmmerce.Sellers.SellerProductCategoryActivity;
import com.example.ecmmerce.Model.Users;
import com.example.ecmmerce.Prevalent.Prevalent;
import com.example.ecmmerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText inputnumber, inputpassword;
    private Button loginbtn;
    private ProgressDialog progressDialog;
    //private CheckBox chboxremmberme;
    TextView adminLink, notAdminLink, forgetPassword;
    private String parentDBName = "Users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        forgetPassword = findViewById(R.id.forrgetpass);
        inputnumber = findViewById(R.id.login_phone_number);
        inputpassword = findViewById(R.id.login_password);
        loginbtn = findViewById(R.id.login_btn);
        progressDialog = new ProgressDialog(this);
        adminLink = findViewById(R.id.admin_link);
        notAdminLink = findViewById(R.id.not_admin_link);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginuser();
            }
        });
        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbtn.setText("Login as Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDBName = "Admins";

            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbtn.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDBName = "Users";

            }
        });

        //chboxremmberme = findViewById(R.id.remmemberme);
        Paper.init(this);


    }

    private void loginuser() {
        String phone = inputnumber.getText().toString();
        String password = inputpassword.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter Your Phone ...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Your Password ...", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.setTitle("Login Account..");
            progressDialog.setMessage("Wait...........");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            allowaccessaccount(phone, password);
        }
    }

    private void allowaccessaccount(final String phone, final String password) {
//        if (chboxremmberme.isChecked()) {
//            Paper.book().write(Prevalent.userphonekey, phone);
//            Paper.book().write(Prevalent.userpasswordkey, password);
//        }
        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDBName).child(phone).exists()) {
                    Users userdata = dataSnapshot.child(parentDBName).child(phone).getValue(Users.class);
                    if (userdata.getPhone().equals(phone)) {
                        if (userdata.getPassword().equals(password)) {
                            if (parentDBName.equals("Admins")) {
                                Toast.makeText(LoginActivity.this, "Admin Done Logged In..!", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                            } else if (parentDBName.equals("Users")) {
                                Toast.makeText(LoginActivity.this, "Done Logged In..!", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentonlineusers = userdata;
                                startActivity(intent);
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Password Incorrect..!", Toast.LENGTH_LONG).show();
                        }

                    }


                } else {
                    Toast.makeText(LoginActivity.this, "Account with " + phone + "Not Exist", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

