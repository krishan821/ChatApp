package com.example.chatapp2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;


import com.example.chatapp2.databinding.ActivityMainBinding;
import com.example.chatapp2.utilities.Constants;
import com.example.chatapp2.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivityMainBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
        preferenceManager = new PreferenceManager( getApplicationContext() );
        loadUserDetails();
        getToken();

    }



    private void loadUserDetails() {


    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener( this::updateToken );
    }
    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection( Constants.KEY_COLLECTION_USERS ).document(
                        preferenceManager.getString(Constants.KEY_USER_ID )
                );
        documentReference.update( Constants.KEY_FCM_TOKEN, token )
                .addOnFailureListener( e -> showToast( "Unable to update Token" ) );
    }

    private void signOut() {
        showToast( "Signing out..." );
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection( Constants.KEY_COLLECTION_USERS ).document(
                        preferenceManager.getString(Constants.KEY_USER_ID )
                );
        HashMap<String , Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN , FieldValue.delete() );
        documentReference.update( updates )
                .addOnSuccessListener( unused -> {
                    preferenceManager.clear();
                    startActivity( new Intent(getApplicationContext(), SignInActivity.class) );
                    finish();
                } )
                .addOnFailureListener( e -> showToast( "Unable to Sign Out" ) );
    }
}