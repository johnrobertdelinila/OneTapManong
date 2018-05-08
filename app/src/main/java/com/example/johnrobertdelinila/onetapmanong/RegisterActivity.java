package com.example.johnrobertdelinila.onetapmanong;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private TextView text_login;
    private Button btn_sign_up;
    private EditText edit_firstname, edit_lastname, edit_email, edit_password, edit_confirm;
    private FirebaseAuth mAuth;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edit_firstname.getText().toString().trim())) {
                    Toast.makeText(RegisterActivity.this, "Firstname", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edit_lastname.getText().toString().trim())) {
                    Toast.makeText(RegisterActivity.this, "Lastname", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edit_email.getText().toString().trim())) {
                    Toast.makeText(RegisterActivity.this, "Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edit_password.getText().toString().trim())) {
                    Toast.makeText(RegisterActivity.this, "Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edit_confirm.getText().toString().trim())) {
                    Toast.makeText(RegisterActivity.this, "Confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!edit_password.getText().toString().trim().equals(edit_confirm.getText().toString().trim())) {
                    Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("NAME", edit_firstname + " " + edit_lastname);

                MaterialDialog.Builder builder = new MaterialDialog.Builder(RegisterActivity.this)
                        .title("Signing up")
                        .content("Please wait")
                        .progress(true, 0);
                final MaterialDialog loading = builder.build();
                loading.show();
                mAuth.createUserWithEmailAndPassword(edit_email.getText().toString().trim(), edit_password.getText().toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String displayName = edit_firstname.getText().toString().trim() + " " + edit_lastname.getText().toString().trim();
                                if (user != null) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(displayName)
                                            .build();
                                    user.updateProfile(profileUpdates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    loading.dismiss();
                                                    Snackbar.make(relativeLayout, "" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void init() {
        // App Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
//        final Drawable upArrow;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
//        }
//        else {
//            upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        }
        upArrow.setColorFilter(getResources().getColor(R.color.backArrow), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        RegisterActivity.this.setTitle("");
        // UI
        text_login = findViewById(R.id.text_login);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        edit_firstname = findViewById(R.id.edit_firstname);
        edit_lastname = findViewById(R.id.edit_lastname);
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        edit_confirm = findViewById(R.id.edit_confirm);
        relativeLayout = findViewById(R.id.relativeLayout);
        // Firebase
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
