package com.example.ecmmerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecmmerce.Prevalent.Prevalent;
import com.example.ecmmerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {
    private String check = "";
    private TextView pageTitle, titleQuestion;
    private EditText phoneNumber, question1, question2;
    private Button verfiyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        check = getIntent().getStringExtra("check");
        pageTitle = findViewById(R.id.title_tv);
        titleQuestion = findViewById(R.id.title_questions);
        phoneNumber = findViewById(R.id.find_phone_number);
        question1 = findViewById(R.id.question_1);
        question2 = findViewById(R.id.question_2);
        verfiyBtn = findViewById(R.id.verfiy_btn);

    }

    @Override
    protected void onStart() {
        super.onStart();
        phoneNumber.setVisibility(View.GONE);
        if (check.equals("setting")) {
            pageTitle.setText("Set Questions ");
            titleQuestion.setText("Answer Following Security Questions.. ");
            verfiyBtn.setText("Set ..");
            displayPreviousAnswer();
            verfiyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAnswers();
                }
            });


        } else if (check.equals("login")) {
            phoneNumber.setVisibility(View.VISIBLE);
            verfiyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verfiyuser();
                }
            });
        }
    }

    private void verfiyuser() {
        final String phone = phoneNumber.getText().toString();
        final String ans1 = question1.getText().toString().toLowerCase();
        final String ans2 = question2.getText().toString().toLowerCase();

        if (!phone.equals("") && !ans1.equals("") && !ans2.equals("")) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(phone);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String mphone = dataSnapshot.child("phone").getValue().toString();
                        if (dataSnapshot.hasChild("Security Questions")) {
                            String answer1 = dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String answer2 = dataSnapshot.child("Security Questions").child("answer2").getValue().toString();
                            if (!answer1.equals(ans1)) {
                                Toast.makeText(ResetPasswordActivity.this, "Answer 1 Not Correct ", Toast.LENGTH_LONG).show();
                            } else if (!answer2.equals(ans2)) {
                                Toast.makeText(ResetPasswordActivity.this, "Answer 2 Not Correct ", Toast.LENGTH_LONG).show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New Password");
                                final EditText newpassword = new EditText(ResetPasswordActivity.this);
                                newpassword.setHint("Your New Password ...");
                                builder.setView(newpassword);
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!newpassword.getText().toString().equals("")) {
                                            ref.child("password").setValue(newpassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(ResetPasswordActivity.this,
                                                                        "Done ! ", Toast.LENGTH_LONG).show();
                                                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                    }
                                });
                                builder.show();
                            }

                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "No Security Questions !", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "Phone is wrong !", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            Toast.makeText(ResetPasswordActivity.this, "Complete Form ", Toast.LENGTH_LONG).show();
        }


    }

    private void setAnswers() {
        String answer1 = question1.getText().toString().toLowerCase();
        String answer2 = question2.getText().toString().toLowerCase();
        if (question1.equals("") && question2.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Answer This Questions ", Toast.LENGTH_LONG).show();

        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(Prevalent.currentonlineusers.getPhone());
            HashMap<String, Object> usermap = new HashMap<>();
            usermap.put("answer1", answer1);
            usermap.put("answer2", answer2);
            reference.child("Security Questions").updateChildren(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "Done ! ", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

    }

    private void displayPreviousAnswer() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Prevalent.currentonlineusers.getPhone());
        reference.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ans1 = dataSnapshot.child("answer1").getValue().toString();
                    String ans2 = dataSnapshot.child("answer2").getValue().toString();
                    question1.setText(ans1);
                    question2.setText(ans2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
