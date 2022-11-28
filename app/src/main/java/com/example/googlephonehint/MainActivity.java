package com.example.googlephonehint;

import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;

public class MainActivity extends AppCompatActivity {

    Button getPhoneNum;
    TextView showPhoneNum;

    ActivityResultLauncher<IntentSenderRequest> phoneNumberHintIntentResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                                try {
                                    String phoneNumber = Identity.getSignInClient(getApplicationContext()).getPhoneNumberFromIntent(result.getData());
                                    showPhoneNum.setText(phoneNumber);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        }
                    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPhoneNum = findViewById(R.id.getPhoneNum);
        showPhoneNum = findViewById(R.id.showPhoneNum);

        getPhoneNum.setOnClickListener(view -> {
            GetPhoneNumberHintIntentRequest request = GetPhoneNumberHintIntentRequest.builder().build();

            Identity.getSignInClient(MainActivity.this)
                    .getPhoneNumberHintIntent(request)
                    .addOnSuccessListener(result -> {
                        try {
                            IntentSender intentSender = result.getIntentSender();
                            phoneNumberHintIntentResultLauncher.launch(new IntentSenderRequest.Builder(intentSender).build());
                        } catch (Exception e) {
                            Log.i("Error launching", "error occurred in launching Activity result");
                        }
                    })
                    .addOnFailureListener(e -> Log.i("Failure occurred", "Failure getting phone number"));

        });
    }
}