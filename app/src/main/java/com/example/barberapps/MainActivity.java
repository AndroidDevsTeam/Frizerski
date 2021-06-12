package com.example.barberapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
private AppCompatButton odjavise;
private FirebaseAuth mAuth;
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        odjavise = findViewById(R.id.btnOdjavise);
        mAuth = FirebaseAuth.getInstance();

        odjavise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                odjava();
            }
        });

    }

    private void odjava() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this,SplashScreen.class);
        startActivity(intent);
        finish();

    }
}