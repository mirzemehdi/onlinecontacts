package com.firebase.firebaseusers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Data.MessagesAdapter;
import User.Message;

public class MessagesActivity extends AppCompatActivity {
    private String currentUserId,selectedUserId,selectedUserImageUri;
    private EditText messageEditText;
    private Button messageSendButton;
    private FirebaseDatabase database;
    private DatabaseReference myRef,reverse_myRef;

    private FirebaseAuth mAuth;
    private MessagesAdapter messagesAdapter;
    private RecyclerView messageRecylerView;
    private List<Message> messageList;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();


        FirebaseUser currentUser=mAuth.getCurrentUser();

        selectedUserId=getIntent().getStringExtra("selectedUserId");
        selectedUserImageUri=getIntent().getStringExtra("selectedUserImageUri");
        if (currentUser!=null) {
            currentUserId=currentUser.getUid();


        }
        myRef=database.getReference("Messages").child(currentUserId+selectedUserId);
        reverse_myRef=database.getReference("Messages").child(selectedUserId+currentUserId);



        messageRecylerView=(RecyclerView)findViewById(R.id.messageRecylerView);
        messageRecylerView.setLayoutManager(new LinearLayoutManager(MessagesActivity.this));
        messageRecylerView.setHasFixedSize(true);
        messageList=new ArrayList<>();







        messagesAdapter=new MessagesAdapter(messageList,MessagesActivity.this,currentUserId,selectedUserId,selectedUserImageUri);
        messageRecylerView.setAdapter(messagesAdapter);

        messageEditText=(EditText)findViewById(R.id.messageEditText);
        messageSendButton=(Button)findViewById(R.id.messageSendButton);
        messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


    }

    private void sendMessage() {
        Message message=new Message(currentUserId,messageEditText.getText().toString().trim());
        HashMap<String,String> messageHashMap=new HashMap<>();
        messageHashMap.put("message",message.getMessage());
        messageHashMap.put("userId",message.getUserId());
        myRef.push().setValue(messageHashMap);
        reverse_myRef.push().setValue(messageHashMap);

        messageEditText.setText("");

    }

    @Override
    protected void onStart() {
        super.onStart();
        readMessages();
    }

    private void readMessages() {

        messageList.clear();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               Log.d("Messages",dataSnapshot.toString());
                 Message message=dataSnapshot.getValue(Message.class);
//                Log.d("message",dataSnapshot.getValue(Message.class).getMessage());

                messageList.add(message);
              messagesAdapter.notifyDataSetChanged();
              messageRecylerView.scrollToPosition(messageList.size()-1);


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
        });
    }
}
