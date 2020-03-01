package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journalapp.model.SuitcaseJournal;
import com.example.journalapp.ui.MyRecyclerScroll;
import com.example.journalapp.ui.RecyclerJournalAdapter;
import com.example.journalapp.util.JournalApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class JournalListActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser user;
    StorageReference storageReference;
    FloatingActionButton fab;
    Animation hideMediaPlayer,showmediaplayer;


    FirebaseFirestore db=FirebaseFirestore.getInstance();

    ArrayList<SuitcaseJournal> suitcaseJournalArrayList;
    RecyclerView recyclerView;
    RecyclerJournalAdapter adapter;

    CollectionReference collectionReference=db.collection("Journal");
    TextView noentryJournal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        noentryJournal=findViewById(R.id.listnothoughts);
        recyclerView=findViewById(R.id.rv);
        fab=findViewById(R.id.fab);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        suitcaseJournalArrayList=new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user!=null && firebaseAuth!=null){
                    startActivity(new Intent(JournalListActivity.this,PostJournalActivity.class));

                }
            }
        });

        recyclerView.setOnScrollListener(new MyRecyclerScroll() {
            @Override
            public void show() {
                showmediaplayer= AnimationUtils.loadAnimation(JournalListActivity.this,R.anim.show_media_player);
                fab.clearAnimation();
                fab.setAnimation(showmediaplayer);
                fab.getAnimation().start();
            }

            @Override
            protected void hide() {
                hideMediaPlayer= AnimationUtils.loadAnimation(JournalListActivity.this,R.anim.hide_media_player);
                fab.clearAnimation();
                fab.setAnimation(hideMediaPlayer);
                fab.getAnimation().start();
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
//            case R.id.addjounalbtn:
//                if (user!=null && firebaseAuth!=null){
//                    startActivity(new Intent(JournalListActivity.this,PostJournalActivity.class));
//
//                }
//                break;
            case R.id.sighoutbtn:
                if (user!=null && firebaseAuth!=null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalListActivity.this,MainActivity.class));
                }
                break;




        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId", JournalApi.getINSTANCE().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){

                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                                SuitcaseJournal journal=snapshot.toObject(SuitcaseJournal.class);

                                suitcaseJournalArrayList.add(journal);
                            }

                            adapter=new RecyclerJournalAdapter(JournalListActivity.this,suitcaseJournalArrayList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();


                        }else {
                            noentryJournal.setVisibility(View.VISIBLE);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
