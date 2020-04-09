package com.example.ecmmerce.Buyers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecmmerce.Prevalent.Prevalent;
import com.example.ecmmerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText fullnameET, userPhoneET, adressET;
    private TextView profilechangetextbtn, closetextbtn, savetextbtn;
    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storeprofilePictureRef;
    private String checer = "";
    private Button securityQuestionsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeprofilePictureRef= FirebaseStorage.getInstance().getReference().child("Profile Picture ");
        setContentView(R.layout.activity_settings);
        securityQuestionsBtn=findViewById(R.id.security_questions_btn);
        profileImageView = findViewById(R.id.setting_profileimage);
        fullnameET = findViewById(R.id.setting_full_name);
        userPhoneET = findViewById(R.id.setting_phone_number);
        adressET = findViewById(R.id.setting_address);
        profilechangetextbtn = findViewById(R.id.profile_image_change);
        closetextbtn = findViewById(R.id.close_setting);
        savetextbtn = findViewById(R.id.updte_setting);
        userInfoDisplay(profileImageView, fullnameET, userPhoneET, adressET);
        securityQuestionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this,ResetPasswordActivity.class);
                intent.putExtra("check","setting");
                startActivity(intent);
            }
        });
        closetextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        savetextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checer.equals("clicked")) {
                    userInfoSaved();

                } else {
                    updateOnlyUserInfo();
                }
            }
        });
        profilechangetextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checer = "clicked";
                CropImage.activity(imageUri).setAspectRatio(1, 1)
                        .start(SettingsActivity.this);

            }
        });

    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> usermap = new HashMap<>();
        usermap.put("name", fullnameET.getText().toString());
        usermap.put("address", adressET.getText().toString());
        usermap.put("phoneoreder", userPhoneET.getText().toString());
        ref.child(Prevalent.currentonlineusers.getPhone()).updateChildren(usermap);


        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Done ... ", Toast.LENGTH_LONG).show();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Error Try Again", Toast.LENGTH_LONG);
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(fullnameET.getText().toString())) {
            Toast.makeText(this, "Enter Your Name", Toast.LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(userPhoneET.getText().toString())) {
            Toast.makeText(this, "Enter Your Phone", Toast.LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(adressET.getText().toString())) {
            Toast.makeText(this, "Enter Your Address", Toast.LENGTH_LONG).show();

        } else if (checer.equals("clicked")) {
            uploadImage();
        }

    }

    private void uploadImage() {
        if (imageUri != null) {
            final StorageReference fileRef = storeprofilePictureRef.child(Prevalent.currentonlineusers.getPhone() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> usermap = new HashMap<>();
                        usermap.put("name", fullnameET.getText().toString());
                        usermap.put("address", adressET.getText().toString());
                        usermap.put("phoneoreder", userPhoneET.getText().toString());
                        usermap.put("image", myUrl);
                        ref.child(Prevalent.currentonlineusers.getPhone()).updateChildren(usermap);

                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Done ... ", Toast.LENGTH_LONG).show();
                        finish();
                    } else {

                        Toast.makeText(SettingsActivity.this, "Error ... ", Toast.LENGTH_LONG).show();


                    }

                }
            });
        }else {
            Toast.makeText(SettingsActivity.this,"Image Not Selected  ... ",Toast.LENGTH_LONG).show();

        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullnameET, final EditText userPhoneET, final EditText adressET) {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineusers.getPhone());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("image").exists()) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                        fullnameET.setText(name);
                        userPhoneET.setText(phone);
                        adressET.setText(address);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
