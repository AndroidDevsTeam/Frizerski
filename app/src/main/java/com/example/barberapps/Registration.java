package com.example.barberapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

public class Registration extends AppCompatActivity {
private TextView link;
private TextInputEditText username, password, potvrdiSifru;
private AppCompatButton register;
private ProgressBar progressBar;
private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        link = findViewById(R.id.link);
        username = findViewById(R.id.txUsernameR);
        password = findViewById(R.id.txPasswordR);
        register = findViewById(R.id.btnregister);
        progressBar = findViewById(R.id.registerprogressbar);
        potvrdiSifru = findViewById(R.id.txtPotvrdiSifru);
        mAuth = FirebaseAuth.getInstance();







       register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String email = username.getText().toString();
               String pass = password.getText().toString();
                String confirm_pass = potvrdiSifru.getText().toString();

                //provjeri da li su polja ispunjena
               if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass) || !TextUtils.isEmpty(confirm_pass)){

                   if (pass.equals(confirm_pass)){
                       progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    sendtoMain();
                                }else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(),"Greska: " + error,Toast.LENGTH_LONG).show();
                                }

                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });
                   }else{
                       Toast.makeText(getApplicationContext(),"Sifre se ne poklapaju : ",Toast.LENGTH_LONG).show();


                   }
               }

           }
       });





        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pokreniLogin();
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
           sendtoMain();


        }
    }

    private void sendtoMain() {
        Intent intent = new Intent(Registration.this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void pokreniLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

}