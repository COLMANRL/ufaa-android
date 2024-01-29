package com.example.ufaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.ufaapp.model.UserModel;
import com.example.ufaapp.utils.FirebaseUtil;
import com.google.firebase.messaging.FirebaseMessaging;


public class ProfileActivity extends AppCompatActivity {

    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    EditText idNumberInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.drawer_activity_profile);


        logoutBtn = findViewById(R.id.logout_btn);

//        getUserData();

//        updateProfileBtn.setOnClickListener(v -> updateBtnClick());

        logoutBtn.setOnClickListener(v -> FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Clear user verification flag from SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("isUserVerified");
                editor.apply();

                // Logout logic
                FirebaseUtil.logout();
                Intent intent = new Intent(ProfileActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }));



    }

}
