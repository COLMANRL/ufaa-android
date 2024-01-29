package com.example.ufaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.ufaapp.model.UserModel;
import com.example.ufaapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText usernameInput;
    EditText idNumberInput;
    Button letMeInBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_login_username);
        ImageView imageView = findViewById(R.id.imageViewLogo3);
        Glide.with(this)
                .load(R.drawable.newlogo)
                .override(500, 400) // Resize the image
                    // Adjust cropping to fit the new size
                .into(imageView);


        usernameInput = findViewById(R.id.login_username);
        idNumberInput = findViewById(R.id.login_id_number);
        letMeInBtn = findViewById(R.id.login_let_me_in_btn);
        progressBar =findViewById(R.id.login_progress_bar);

        phoneNumber = getIntent().getExtras().getString("phone");
        getUsername();
        // Limit the username to 100 characters
        usernameInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});

        // Limit the ID number to 20 characters
        idNumberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});


        letMeInBtn.setOnClickListener((v -> {
            setUsername();
        }));


    }

    void setUsername(){

        String username = usernameInput.getText().toString();
        String idNumber = idNumberInput.getText().toString();
        if(username.isEmpty() || username.length()<3){
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        if (idNumber.isEmpty()) {
            idNumberInput.setError("Please enter your ID number");
            return;
        }
        setInProgress(true);
        if(userModel!=null){
            userModel.setUsername(username);
            userModel.setIdNumber(idNumber);
        }else{
            userModel = new UserModel(phoneNumber,username,idNumber,Timestamp.now(),FirebaseUtil.currentUserId());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                   Intent intent = new Intent(LoginUsernameActivity.this,SearchActivity.class);
                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                   startActivity(intent);
                }
            }
        });

    }

    void getUsername(){
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                  userModel =    task.getResult().toObject(UserModel.class);
                 if(userModel!=null){
                     usernameInput.setText(userModel.getUsername());
                     idNumberInput.setText(userModel.getIdNumber());
                 }
                }
            }
        });
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }

        return false;
    }

}