package com.example.barberapps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registration extends AppCompatActivity {
private TextView link;
private TextInputEditText imeprezime, telefon,username, password, potvrdiSifru;
private AppCompatButton register;
private ProgressBar progressBar;
private FirebaseAuth mAuth;
CircleImageView circleImageView;

private Uri mImageUri;
String url,name;

FirebaseFirestore fStore;


private StorageReference mStoragereference;

private DatabaseReference mDatabaserefernce;


String type;


UploadTask uploadTask;
FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
String currentuid;
private final static int PICK_FILE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        fStore = FirebaseFirestore.getInstance();

        imeprezime = findViewById(R.id.txImePrezime);
        telefon = findViewById(R.id.txtBrTelefona);
        link = findViewById(R.id.link);
        username = findViewById(R.id.txUsernameR);
        password = findViewById(R.id.txPasswordR);
        register = findViewById(R.id.btnregister);
        progressBar = findViewById(R.id.registerprogressbar);
        potvrdiSifru = findViewById(R.id.txtPotvrdiSifru);
        mAuth = FirebaseAuth.getInstance();
        circleImageView = findViewById(R.id.profileimage);

        mStoragereference = FirebaseStorage.getInstance().getReference("Images");
        mDatabaserefernce = FirebaseDatabase.getInstance().getReference("Images");



        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              izaberisliku();


            }

            private void izaberisliku() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_FILE);

            }
        });






//button za dodavanje slike





       register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String email = username.getText().toString();
               String pass = password.getText().toString();
                String confirm_pass = potvrdiSifru.getText().toString();
                String simeprezime = imeprezime.getText().toString();
                String stelefon = telefon.getText().toString().trim();

                //provjeri da li su polja ispunjena
               if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass) || !TextUtils.isEmpty(confirm_pass)){

                   if (pass.equals(confirm_pass)){
                       progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    currentuid = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = fStore.collection("users").document(currentuid);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("ImePrezime",simeprezime);
                                    user.put("Email",email);
                                    user.put("Sifra",pass);
                                    user.put("Telefon",stelefon);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TAG","onSuccess. Racun je kreiran" + currentuid);
                                        }
                                    });
                                    sendtoMain();
                                    UploadFile();

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE || requestCode == RESULT_OK ||
        data !=null || data.getData() !=null) {

            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(circleImageView);


        }else{
            Toast.makeText(getApplicationContext(),"Nema fajla",Toast.LENGTH_LONG).show();
        }

    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private void UploadFile() {
        String idUid = mAuth.getCurrentUser().getUid().toString();

    if (mImageUri != null){
        StorageReference filereference = mStoragereference.child(idUid
        +"."+getFileExt(mImageUri));
        filereference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Uspjesno spremljeno",Toast.LENGTH_LONG).show();
            Upload upload = new Upload(username.getText().toString().trim(),
                    taskSnapshot.getUploadSessionUri().toString());
            String uploadID = mDatabaserefernce.push().getKey();
            mDatabaserefernce.child(uploadID).setValue(upload);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
        Toast.makeText(getApplicationContext(),"Greska: " + e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });


    }else {
        Toast.makeText(Registration.this,"Nije izabraqn fajl", Toast.LENGTH_LONG).show();

    }

    }



}