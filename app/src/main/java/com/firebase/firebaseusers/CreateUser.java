package com.firebase.firebaseusers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.dd.processbutton.iml.SubmitProcessButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import Data.ProgressGenerator;
import User.User;

public class CreateUser extends AppCompatActivity implements ProgressGenerator.OnCompleteListener {
    private EditText usernameEditText,passwordEditText,nameEditText,surnameEditText;
    private ActionProcessButton registerButton;
    private ImageButton chooseImage;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference storageReference;

    private Uri imageUri=null;
    private User user;
    private ProgressGenerator progressGenerator;
    private boolean connected=false;
    private static final int REQUEST_CODE=1;
    private static final String DEFAULT_PP ="https://firebasestorage.googleapis.com/v0/b/fir-contacts-ddc64.appspot.com/o/ProfilePictures%2Fuserprofile.png?alt=media&token=3915b03a-296f-4919-a02f-ca4486ab5b6b";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        checkInternetConnection();
        setContentView(R.layout.activity_create_user);
        progressGenerator = new ProgressGenerator(this);
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("Users");
        Log.d("DatabaseRef",database.getReference().toString());
        storageReference= FirebaseStorage.getInstance().getReference("ProfilePictures");
        //progressDialog=new ProgressDialog(this);
        usernameEditText=(EditText)findViewById(R.id.usernameEditTextC);
        passwordEditText=(EditText)findViewById(R.id.passwordEditTextC);
        nameEditText=(EditText)findViewById(R.id.nameEditTextC);
        surnameEditText=(EditText)findViewById(R.id.surnameEditTextC);
        registerButton=(ActionProcessButton) findViewById(R.id.createAccountButtonC);
        chooseImage=(ImageButton) findViewById(R.id.profileImage);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //FOR CREATING Fake Accounts for testing
              /*  for (int i=20;i<30;i++){

                    final String testUsername="Test"+String.valueOf(i)+"@gmail.com";
                    final String testPassword="123456";

                    final int finalI = i;
                    mAuth.createUserWithEmailAndPassword(testUsername,testPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            String testUserId=mAuth.getCurrentUser().getUid();
                            String testName="Test"+String.valueOf(finalI);
                            user = new User(testUsername, testPassword, testName, " ", testUserId, DEFAULT_PP);
                            myRef.child(testUserId).setValue(user);


                        }
                    });
                }*/


                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String name = nameEditText.getText().toString();
                final String surname = surnameEditText.getText().toString();




                if (!(username.isEmpty()) && !(password.isEmpty())){
                    if (textInputsOK(username,password)) {

                        checkInternetConnection();


                        if (connected) {
                            progressGenerator.start(registerButton);
                            //progressDialog.setMessage("Creating Contact...");
                            //progressDialog.show();
                            mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(CreateUser.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    //Create Account is successful
                                    if (task.isSuccessful()) {


                                        Log.d("CreateAccount", "Account is created");

                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        final String userid = firebaseUser.getUid();
                                        //Photo is chosen
                                        if (imageUri != null) {

                                            StorageReference image = storageReference.child(imageUri.getLastPathSegment());
                                            image.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Log.d("ImageUpload", "Image uploading...");
                                                    imageUri = taskSnapshot.getDownloadUrl();
                                                    user = new User(username, password, name, surname, userid, imageUri.toString());
                                                    myRef.child(userid).setValue(user);


                                                }

                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("ImageUpload", "Image couldn't uploading...");

                                                }
                                            });

                                        }
                                        //Photo is not chosen
                                        else {
                                            Log.d("PhotoChose", "Photo is not choosen");
                                            user = new User(username, password, name, surname, userid, DEFAULT_PP);
                                            myRef.child(userid).setValue(user);

                                        }


                                        Toast.makeText(CreateUser.this, "Contact is created ", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(CreateUser.this, MainActivity.class));
                                        finish();
                                        // progressDialog.dismiss();


                                    }
                                    // Failed Create Account
                                    else {
                                        progressGenerator.stop(registerButton);

                                        // progressDialog.dismiss();
                                        Log.d("CreateAccount", "Failed Create Account");
                                        Toast.makeText(CreateUser.this, username + " aleady exists", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        //There is no internet

                        else {

                            Toast.makeText(CreateUser.this, "There is no internet connection", Toast.LENGTH_LONG).show();

                        }

                    }
            }
            // Username or password is empty
            else {
                    Toast.makeText(CreateUser.this,"Username or password can't be empty",Toast.LENGTH_LONG).show();
                }
            }
        });


        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent choosePicture=new Intent(Intent.ACTION_GET_CONTENT);
                choosePicture.setType("image/*");
                startActivityForResult(choosePicture,REQUEST_CODE);

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

    public boolean emailCheck(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean textInputsOK(String mail,String password){
        if (emailCheck(mail)){
            if (password.length()<6){
                Toast.makeText(this,"Password must be at least 6 characters",Toast.LENGTH_LONG).show();
                return false;
            }
            else{
                return true;

            }
        }
        else{
            Toast.makeText(this,"A valid email is required",Toast.LENGTH_LONG).show();
            return false;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUEST_CODE&&resultCode==RESULT_OK){
            imageUri=data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1).setAllowRotation(true)
                    .start(CreateUser.this);



        }

        //Crop image

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                chooseImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }
//when create account is finished
    @Override
    public void onComplete() {


    }
}
