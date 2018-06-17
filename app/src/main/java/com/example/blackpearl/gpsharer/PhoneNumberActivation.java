package com.example.blackpearl.gpsharer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneNumberActivation extends AppCompatActivity {

    private EditText nameEditText;
    private EditText phoneNumberEditText;
    private EditText verifyCodeET;
    private Button signUpBtn;
    private Button verifyCodeBtn;

    private CountryCodePicker countryCodePicker;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    String fullName;
    String phoneNumber;


    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_activation);

        nameEditText = findViewById(R.id.nameET_id);
        phoneNumberEditText = findViewById(R.id.phnET_id);
        signUpBtn = findViewById(R.id.signupbtn_id);
        verifyCodeET = findViewById(R.id.verificationCode_id);
        verifyCodeBtn = findViewById(R.id.verifybtn_id);
        countryCodePicker = findViewById(R.id.countryCodePicker_id);

        mAuth = FirebaseAuth.getInstance();


        fullName = nameEditText.getText().toString();
        phoneNumber = phoneNumberEditText.getText().toString();


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendVerificationCode();
                phoneNumberEditText.setEnabled(false);

            }
            //  }
        });

        verifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationCode = verifyCodeET.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                signInWithPhoneAuthCredential(credential);
            }
        });


    }


    private void sendVerificationCode() {

        String countrycode = "+" + countryCodePicker.getSelectedCountryCode();
        String phone = countrycode + phoneNumberEditText.getText().toString();

        if (phone.isEmpty()) {
            phoneNumberEditText.setError("Phone number required");
            phoneNumberEditText.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            phoneNumberEditText.setError("Phone number invalid");
            phoneNumberEditText.requestFocus();
            return;
        }
        if (nameEditText.getText().toString().isEmpty()) {

            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                PhoneNumberActivation.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }


        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(PhoneNumberActivation.this, "Verification code error", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;

            verifyCodeET.setVisibility(View.VISIBLE);
            signUpBtn.setVisibility(View.INVISIBLE);


            // ...
        }

    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(PhoneNumberActivation.this, MapsActivity2.class);
                            startActivity(intent);
                            finish();

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });

    }
}
