package com.firebase.firebaseusers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Data.SavedAccount;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText,passwordEditText;
    private Button loginButton,registerButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean connected = false;
    private SavedAccount savedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupItems();
        checkInternetConnection();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser=firebaseAuth.getCurrentUser();
                if (currentUser!=null) {
                    Log.d("MainActivity", "Loged In");
                }
                    else {
                    Log.d("MainActivity", "Failed Log in");
                }

                }
            };


        // Pass Create Activity
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passCreateAccountActivity=new Intent(MainActivity.this,CreateUser.class);
                startActivity(passCreateAccountActivity);

            }
        });




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (!username.isEmpty() && !password.isEmpty()){
                    checkInternetConnection();
                    if (connected){

                        savedAccount.setUsername(username);
                        savedAccount.setPassword(password);

                        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(
                                MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(MainActivity.this, "Loged in", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(MainActivity.this, EntranceActivity.class));
                                            finish();

                                        } else {
                                            Toast.makeText(MainActivity.this, "Username or password is incorrect", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                        );
                    }
                    else{
                        Toast.makeText(MainActivity.this, "There is no internet connection", Toast.LENGTH_LONG).show();

                    }


            }

            else{
                    Toast.makeText(MainActivity.this, "Fields are empty", Toast.LENGTH_LONG).show();
                }

            }
        });



    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
    }


    public void setupItems(){

        usernameEditText=(EditText)findViewById(R.id.usernameEditText);
        passwordEditText=(EditText)findViewById(R.id.passwordEditText);
        loginButton=(Button)findViewById(R.id.loginButton);
        registerButton=(Button)findViewById(R.id.createAccountButton);
        mAuth=FirebaseAuth.getInstance();
        savedAccount=new SavedAccount(MainActivity.this);
        usernameEditText.setText(savedAccount.getUsername());
        passwordEditText.setText(savedAccount.getPassword());

    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth!=null)
            mAuth.removeAuthStateListener(mAuthListener);
    }











    // Settings of menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
     menu.findItem(R.id.addPostButton).setVisible(false);
     menu.findItem(R.id.signOutButton).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.signOutButton:{
                // Sign out from current Account
                Log.d("signOut","sign out");

            }
            break;


        }

        return super.onOptionsItemSelected(item);
    }



}
