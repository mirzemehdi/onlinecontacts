package com.firebase.firebaseusers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EntranceActivity extends AppCompatActivity {

    private Button usersButton;
    private Button postsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        usersButton=(Button)findViewById(R.id.usersButton);
        postsButton=(Button)findViewById(R.id.postsButton);

        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO Go To the Users Activity
                startActivity(new Intent(EntranceActivity.this,UsersActivity.class));

            }
        });


        postsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO Go To the Posts Activity
                startActivity(new Intent(EntranceActivity.this,PostsActivity.class));

            }
        });

    }


}
