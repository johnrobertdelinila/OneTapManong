package com.example.johnrobertdelinila.onetapmanong;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
//import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView text_register, text_forgot;
    private Button btn_twitter_sign_in, btn_facebook_sign_in, btn_google_sign_in;
    private TextInputEditText edit_email, edit_password;
    private FirebaseAuth mAuth;
    private Button btn_email_sign_in;
    private RelativeLayout relativeLayout;

    private static final int SOCIAL_MEDIA_SIGN_IN = 1996;
    private List<AuthUI.IdpConfig> google_provider, facebook_provider, twitter_provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onAuthChangedListener();
        init();

        text_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btn_email_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_email.getText().toString().trim();
                String password = edit_password.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    return;
                }

                MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this)
                        .title("Signing in")
                        .content("Please wait..")
                        .progress(true, 0);
                final MaterialDialog loading = builder.build();
                loading.show();
                Log.e("SIGNING", "SIGNING IN");
                mAuth.signInWithEmailAndPassword(email,password )
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                loading.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loading.dismiss();
                                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        text_forgot.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this)
                        .title("Just a moment")
                        .content("Please wait..")
                        .progress(true, 0);
                final MaterialDialog loading = builder.build();
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Forgot Password");
                dialog.setMessage("Please enter your email.");
                View view = getLayoutInflater().inflate(R.layout.layout_forgot_password, null);
                float dpi = MainActivity.this.getResources().getDisplayMetrics().density;
                dialog.setView(view, (int)(19*dpi), (int)(5*dpi), (int)(14*dpi), (int)(5*dpi));
                final EditText layout_edit_email = view.findViewById(R.id.edit_email);
                dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loading.show();
                        final String email = layout_edit_email.getText().toString().trim();
                        mAuth.sendPasswordResetEmail(email)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        loading.dismiss();
                                        Toast.makeText(MainActivity.this, "A Reset Email has been sent to \'" + email + "\'. Click the link on the email to reset password.", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loading.dismiss();
                                        Log.e("SOCIAL ERR", e.getMessage());
                                        Snackbar.make(relativeLayout, "" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        btn_twitter_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(twitter_provider)
                        .build(),
                    SOCIAL_MEDIA_SIGN_IN
                );
            }
        });

        btn_facebook_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(facebook_provider)
                                .build(),
                        SOCIAL_MEDIA_SIGN_IN
                );
            }
        });

        btn_google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(google_provider)
                                .build(),
                        SOCIAL_MEDIA_SIGN_IN
                );
            }
        });

    }

    private void onAuthChangedListener() {
        FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(listener);
    }

    private void init() {
        // APP BAR
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        MainActivity.this.setTitle("");
        // UI
        text_register = findViewById(R.id.text_register);
        btn_email_sign_in = findViewById(R.id.btn_sign_in);
        btn_twitter_sign_in = findViewById(R.id.btn_twitter_sign_in);
        btn_facebook_sign_in = findViewById(R.id.btn_facebook_sign_in);
        btn_google_sign_in = findViewById(R.id.btn_google_sign_in);
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        mAuth = FirebaseAuth.getInstance();
        text_forgot = findViewById(R.id.text_forgot);
        relativeLayout = findViewById(R.id.relativeLayout);

        google_provider = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
        );
        facebook_provider = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
        );
        twitter_provider = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SOCIAL_MEDIA_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode != RESULT_OK) {
                if (response.getError().getMessage() != null) {
                    Log.e("ERROR SOCIAL", response.getError().getMessage());
                    Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }

    }

}
