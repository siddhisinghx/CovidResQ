package com.example.roadresq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private  FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.setTitle("CovidResQ");
        initialize();
    }

    void initialize(){
        final EditText textInputLayoutName = findViewById(R.id.textInputName);
        final EditText textInputLayoutEmail = findViewById(R.id.textInputEmail);
        final EditText textInputLayoutPass = findViewById(R.id.textInputPass);
        final EditText textInputPassConfirm = findViewById(R.id.textInputPassConfirm);
        TextView textView = findViewById(R.id.link_login);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });




        Button signUpButton = findViewById(R.id.btn_signupActivity);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 String email = textInputLayoutEmail.getText().toString();
                 String pass = textInputLayoutPass.getText().toString();
                 String passConfirm = textInputPassConfirm.getText().toString();
                 String name = textInputLayoutName.getText().toString();
                onButtonClick(email,pass,name,passConfirm);
                Log.i("abc", "signUpFirebase: "+email+" "+pass+"  ll");
            }
        });



    }

    void onButtonClick(String email, String pass, String name, String passConfirm){

        if(pass.equals(passConfirm)  && !name.isEmpty() && !email.isEmpty() && !pass.isEmpty()){
//
            signUpFirebase(email,pass,name);
        }

        else{
            Toast.makeText(getApplicationContext(),"Please check the details and try again!",Toast.LENGTH_SHORT).show();
        }


    }

    void signUpFirebase(String email, String pass, final String name){

        mAuth = FirebaseAuth.getInstance();



        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            updateUI(name);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(),"SignUp Failed, Please try again!",Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    void updateUI(String name){
        Intent intent = new Intent(SignUpActivity.this,SignUpActivityTwo.class);
        intent.putExtra("name",name);
        startActivity(intent);
    }
}
