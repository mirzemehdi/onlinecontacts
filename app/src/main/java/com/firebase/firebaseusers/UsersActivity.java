package com.firebase.firebaseusers;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.firebaseusers.Interface.ILoadMore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Data.Adapter;
import Data.SavedAccount;
import User.User;

public class UsersActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView usersListRecycler;
    private RecyclerView.Adapter adapter;
    private List<User> usersList;
    private DatabaseReference myref;
    private FirebaseAuth mAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBarUsers;
    private boolean isMax=false;
    private static final int ITEM_COUNT=10;
    private String last_key="null",last_loaded_key="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Toast.makeText(this,"UsersActivity",Toast.LENGTH_LONG).show();
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        progressBarUsers=(ProgressBar)findViewById(R.id.progressBarUsers);
        myref= FirebaseDatabase.getInstance().getReference("Users");
        mAuth=FirebaseAuth.getInstance();
        usersList=new ArrayList<>();
        usersListRecycler=(RecyclerView)findViewById(R.id.usersListRecycler);
        usersListRecycler.setLayoutManager(new LinearLayoutManager(this));
        usersListRecycler.setHasFixedSize(true);

        //TODO We pass UsersList From Firebase



        //Setup Adapter
        adapter=new Adapter(usersListRecycler,usersList,this);

        usersListRecycler.setAdapter(adapter);

        ((Adapter)adapter).setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {

                if(true){
                usersList.add(null);
                adapter.notifyItemInserted(usersList.size()-1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        //Add More Data
                        retrieveData(last_loaded_key);

                        adapter.notifyDataSetChanged();
                        ((Adapter)adapter).setLoaded();


                    }
                },2000);

                }
                else
                    Toast.makeText(UsersActivity.this,"Complete",Toast.LENGTH_SHORT).show();
            }
        });





    }

    public void retrieveData(String lastLoadedKey){

        Log.d("Last node",last_key);


        Log.d("LastLoadedKey",lastLoadedKey);
        if (lastLoadedKey.equals(last_key)){
            isMax=true;
            usersList.remove(usersList.size() - 1);
            adapter.notifyItemRemoved(usersList.size());
            Toast.makeText(UsersActivity.this,"List is complete",Toast.LENGTH_SHORT).show();
        }

        else {
            Query query;
            if (lastLoadedKey.equals(""))
                query=myref.orderByKey().limitToFirst(ITEM_COUNT);
            else {
                query = myref.orderByKey().startAt(lastLoadedKey).limitToFirst(ITEM_COUNT);
                Log.d("LastLoadedKey",lastLoadedKey);
            }

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()){
                        if (!usersList.isEmpty()) {
                            usersList.remove(usersList.size() - 1);
                            adapter.notifyItemRemoved(usersList.size());
                        }
                        else
                            progressBarUsers.setVisibility(View.GONE);

                        for (DataSnapshot data: dataSnapshot.getChildren()){
                            User user = data.getValue(User.class);
                            usersList.add(user);

                            adapter.notifyDataSetChanged();
                        }
                        last_loaded_key=usersList.get(usersList.size()-1).getUserId();
                        if (!last_loaded_key.equals(last_key)){
                            usersList.remove(usersList.size()-1);
                            adapter.notifyDataSetChanged();
                        }
                        else
                            isMax=true;
                    }
                    else {
                        progressBarUsers.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            /*query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/


        }

    }
    public void get_last_key(){
        myref.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               for (DataSnapshot lastData: dataSnapshot.getChildren()){
                   Log.d("dataSnapShot",lastData.toString()+"    \n"+lastData.getKey());
                   last_key=lastData.getKey();
               }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        usersList.clear();
        get_last_key();
        retrieveData("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.signOutButton:{
                mAuth.signOut();
                SavedAccount savedAccount=new SavedAccount(this);
                savedAccount.removeCurrentAccount();
                startActivity(new Intent(UsersActivity.this,MainActivity.class));
                finish();

            }
            break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        onStart();
        swipeRefreshLayout.setRefreshing(false);
    }
}
