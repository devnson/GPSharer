package com.example.blackpearl.gpsharer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    SignInButton btngoogle;
    Button btnphone;


    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 100;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;


    public static final String SHARED_PREFS = "sharedPrefs";
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btngoogle = findViewById(R.id.btnGoogle_id);
        //btnphone = findViewById(R.id.btnPhone_id);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        onClickGoogleButton();

        mAuth = FirebaseAuth.getInstance();
        checkandsignIn();

        /*
        btnphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, PhoneNumberActivation.class);
                startActivity(intent);
                finish();
            }
        });
        */


    }


    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        checkandsignIn();


    }


    public void onClickGoogleButton() {

        btngoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();


            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

    }

    DatabaseReference mDatabaseReference;
    FirebaseDatabase mDatabase;

    String replacedId;

    SecureRandom random = new SecureRandom();
    String randomCode = new BigInteger(30, random).toString(32).toUpperCase();


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // updateUI(user);

                            checkandsignIn();
                            updateUserData();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }

    String mid;
    String musername;
    String mfullname;

    private void checkandsignIn() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mid = user.getEmail();
            musername = user.getDisplayName();
            mfullname = user.getDisplayName();
            passInformation();

            Log.d("mid", mid);
            Log.d("musername", musername);
            Log.d("mfullname", mfullname);

        }
    }

    private void passInformation() {

        Intent intent = new Intent(MainActivity.this, MapsActivity2.class);
        Bundle b = new Bundle();
        if (mid != null) {
            replacedId = mid.replace("@gmail.com", "");
            Log.d("ReplacedId", replacedId);
            b.putString("id", replacedId);
        } else {
            Toast.makeText(MainActivity.this, "Id mismatched", Toast.LENGTH_SHORT).show();
        }
        b.putString("username", musername);
        b.putString("fullname", mfullname);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void updateUserData() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference("Users");
        // Sign in success, update UI with the signed-in user's information
        Log.d(TAG, "signInWithCredential:success");

        checkandsignIn();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("Email", mid);
        userMap.put("Id", replacedId);
        userMap.put("Fullname", mfullname);
        userMap.put("CircleCode", randomCode);
        mDatabaseReference.child(replacedId).updateChildren(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Database added", Toast.LENGTH_SHORT).show();

                Map<String, Object> map = new HashMap<>();
                map.put(replacedId, randomCode);
                mDatabaseReference.child("CircleCodes").updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });

    }
}
