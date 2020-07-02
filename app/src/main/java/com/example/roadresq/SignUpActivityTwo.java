package com.example.roadresq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivityTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_two);
        this.setTitle("CovidResQ");
        initialize();
    }

    void initialize(){

        final TextInputLayout phone = findViewById(R.id.textInputPhone);
        final TextInputLayout bloodGroup = findViewById(R.id.textInputBloodGroup);
        final TextInputLayout gender = findViewById(R.id.textInputGender);
        final TextInputLayout adhaar = findViewById(R.id.textInputAdhaar);
        final TextInputLayout age = findViewById(R.id.textInputAge);



        Button buttonFinish = findViewById(R.id.btn_signupActivityTwo);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 String phoneNum = phone.getEditText().getText().toString();
                 String bloodGroupText = bloodGroup.getEditText().getText().toString();
                 String sex = gender.getEditText().getText().toString();
                 String adhaarNumber = adhaar.getEditText().getText().toString();
                 String ageNumber = age.getEditText().getText().toString();


                if(!phoneNum.isEmpty() && !bloodGroupText.isEmpty() && !sex.isEmpty());
                addToFireBase(phoneNum,bloodGroupText,sex,adhaarNumber,ageNumber);
            }
        });


    }

    void addToFireBase(String number, String blood, String gender, String adhaar, String age){

        String name = getIntent().getStringExtra("name");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, String> map = new HashMap<>();
        map.put("phone",number);
        map.put("bloodGroup",blood);
        map.put("gender",gender);
        map.put("name",name);
        map.put("aadhaar",adhaar);
        map.put("age",age);
        databaseReference.child("data").child(user).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(SignUpActivityTwo.this,NavigationDrawer.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
