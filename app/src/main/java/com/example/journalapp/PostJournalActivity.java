package com.example.journalapp;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journalapp.model.SuitcaseJournal;
import com.example.journalapp.util.JournalApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Objects;

public class PostJournalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EEEE";
    private static final int GALLERY_CODE = 1111;
    ImageView imageView;
    ImageView updateimgbtn;
    TextView usernametv;
    TextView datetv;
    EditText titleedt;
    EditText thoughtedt;
    ProgressBar progressBar;
    Button savebtn;

    String currentusername;
    String currentuserId;

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser user;

    FirebaseFirestore db=FirebaseFirestore.getInstance();

    StorageReference storageReference;
    CollectionReference collectionReference=db.collection("Journal");
    private Uri imageuri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        storageReference= FirebaseStorage.getInstance().getReference();

        imageView =findViewById(R.id.postiv);
        updateimgbtn=findViewById(R.id.postupdateimagebtn);
        usernametv=findViewById(R.id.postusernametv);
        datetv=findViewById(R.id.postdatetv);
        titleedt=findViewById(R.id.postedttitle);
        thoughtedt=findViewById(R.id.postthoughtedt);
        progressBar=findViewById(R.id.postprogressBar);
        savebtn=findViewById(R.id.postsavebtn);
        firebaseAuth=FirebaseAuth.getInstance();
        savebtn.setOnClickListener(this);
        updateimgbtn.setOnClickListener(this);

        progressBar.setVisibility(View.INVISIBLE);
        if (JournalApi.getINSTANCE()!=null){

            currentuserId=JournalApi.getINSTANCE().getUserId();
            currentusername=JournalApi.getINSTANCE().getUsername();

            usernametv.setText(currentusername);
        }

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user=firebaseAuth.getCurrentUser();
                if (user!=null){


                }else {


                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        user=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.postsavebtn:

                saveJournal();

                break;

            case R.id.postupdateimagebtn:
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
                break;
        }
    }

    private void saveJournal() {
        final String title=titleedt.getText().toString().trim();
        final String thought=thoughtedt.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(title)&&
            !TextUtils.isEmpty(thought)&&
            imageuri!=null){
            final StorageReference filepath=storageReference.child("journal_images")
                                                        .child("my_image"+ Timestamp.now().getSeconds());

            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageurl=uri.toString();
                            SuitcaseJournal journal=new SuitcaseJournal();
                            journal.setTitle(title);
                            journal.setThought(thought);
                            journal.setImageUrl(imageurl);
                            journal.setUserId(currentuserId);
                            journal.setUserName(currentusername);
                            journal.setTimeAdded(new Timestamp(new Date()));

                            collectionReference.add(journal)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                            progressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(PostJournalActivity.this,JournalListActivity.class));

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

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
            Toast.makeText(this, "Please Fill All the Required Fields", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_CODE && resultCode==RESULT_OK){

            if (data!=null){

                imageuri=data.getData();
                imageView.setImageURI(imageuri);
            }

        }
    }
}
