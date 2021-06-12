package com.example.barberapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
private TextInputEditText usernameL,passwordL;
private AppCompatButton btnLogin;
private FirebaseAuth mAuth;
private ProgressBar progressBar;
private CheckBox checkBox;
private TextView prijavise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prijavise = findViewById(R.id.prijavises);
        mAuth = FirebaseAuth.getInstance();
        usernameL = findViewById(R.id.txtUsername);
        passwordL = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.login);
        progressBar = findViewById(R.id.loginprogressBar);
        checkBox = findViewById(R.id.checkB);

        //klikni na registraciju

        prijavise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrujse();

            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmail = usernameL.getText().toString();
                String loginPass = passwordL.getText().toString();

                if (!TextUtils.isEmpty(loginEmail) || !TextUtils.isEmpty(loginPass)){
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                sendToMain();
                            }else {

                                String error = task.getException().getMessage();

                                Toast.makeText(getApplicationContext(),"Prijava nije uspjesna: " + error,Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(b){
                               passwordL.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                            }else {
                                passwordL.setTransformationMethod(PasswordTransformationMethod.getInstance());

                            }
                        }
                    });
                }

            }
        });

    }

    private void registrujse() {
        Intent x = new Intent(LoginActivity.this,Registration.class);
        startActivity(x);
        finish();


    }


    private void sendToMain() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();


    }

    ///provjeri da li je korisnik vec prijavljebn
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}