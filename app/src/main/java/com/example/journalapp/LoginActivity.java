package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.journalapp.util.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MMMM";
    Button loginbtn,createAccountbtn;
    AutoCompleteTextView emailedt;
    EditText passedt;
    ProgressBar progressBar;

    FirebaseUser user;

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference collectionReference=db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        loginbtn=findViewById(R.id.loginbtn);
        createAccountbtn=findViewById(R.id.createaccbtn);
        progressBar=findViewById(R.id.loginprogressbar);

        emailedt=findViewById(R.id.emailedt);
        passedt=findViewById(R.id.passedt);

        firebaseAuth=FirebaseAuth.getInstance();
        progressBar.setVisibility(View.INVISIBLE);



        createAccountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginEmailPasswordUser(emailedt.getText().toString().trim(),passedt.getText().toString().trim());

            }
        });


    }

    private void loginEmailPasswordUser(String email, String pass) {
        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(email) &&
            !TextUtils.isEmpty(pass)){

            firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    user=firebaseAuth.getCurrentUser();

                    assert user != null;
                    String currentuserID=user.getUid();

                    collectionReference.whereEqualTo("userId",currentuserID)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                    if (!queryDocumentSnapshots.isEmpty()){
                                        progressBar.setVisibility(View.INVISIBLE);
                                        JournalApi journalApi=JournalApi.getINSTANCE();
                                        for (QueryDocumentSnapshot snapshot :
                                                queryDocumentSnapshots) {
                                            journalApi.setUsername(snapshot.getString("userName"));
                                            journalApi.setUserId(snapshot.getString("userId"));

                                        }
                                        Log.d(TAG, "onEvent: "+journalApi.getUserId()+"   "+journalApi.getUsername());

                                        startActivity(new Intent(LoginActivity.this,PostJournalActivity.class));
                                        finish();
                                    }

                                }
                            });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);


                }
            });


        }else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        }

    }
}
