package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.journalapp.util.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = "YYYY";
    EditText emailedt,passwordedt,usernameedt;
    Button createaccountbtn;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentuser;
    ProgressBar progressBar;

    FirebaseFirestore db=FirebaseFirestore.getInstance();

    CollectionReference collectionReference=db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth=FirebaseAuth.getInstance();

        usernameedt=findViewById(R.id.usernameedtAccount);
        emailedt=findViewById(R.id.emailedtAccount);
        passwordedt=findViewById(R.id.passedtAccount);
        createaccountbtn=findViewById(R.id.createaccbtnAccount);
        progressBar=findViewById(R.id.progressbarAccount);

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentuser=firebaseAuth.getCurrentUser();

                if (currentuser!=null){
                    //user is logged in

                }else {
                    //user Not Logged In
                }

            }
        };


        createaccountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(usernameedt.getText().toString().trim()) &&
                        !TextUtils.isEmpty(emailedt.getText().toString().trim()) &&
                        !TextUtils.isEmpty(passwordedt.getText().toString().trim()) ){

                    String email=emailedt.getText().toString().trim();
                    String password=passwordedt.getText().toString().trim();
                    String userName=usernameedt.getText().toString().trim();

                    createEmailAccount(email,password,userName);
                }else {
                    Toast.makeText(CreateAccountActivity.this,
                            "Please Fill up all The Fields",
                            Toast.LENGTH_SHORT)
                            .show();
                }


            }
        });



    }

    private void createEmailAccount(String email, String password, final String username) {

        if (!TextUtils.isEmpty(email)
        && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(username)){

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                currentuser=firebaseAuth.getCurrentUser();

                                assert currentuser != null;
                                final String currentUserUid=currentuser.getUid();
                                Map<String,String> userObj=new HashMap<>();

                                userObj.put("userId",currentUserUid);
                                userObj.put("userName",username);

                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                if (Objects.requireNonNull(task.getResult()).exists()) {
                                                                    progressBar.setVisibility(View.INVISIBLE);

                                                                    String name = task.getResult()
                                                                            .getString("userName");
                                                                    Log.d(TAG, "onComplete: "+name);

                                                                    JournalApi journalApi = JournalApi.getINSTANCE();
                                                                    journalApi.setUsername(name);
                                                                    journalApi.setUserId(currentUserUid);

                                                                    Intent intent = new Intent(CreateAccountActivity.this, PostJournalActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                                }else {

                                                                }

                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });


                            }else {

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }else {

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentuser=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
