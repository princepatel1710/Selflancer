package com.example.self_lance;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class signup extends AppCompatActivity {

    private EditText edtFirstName, edtLastName, edtEmail, edtPassword, edtUserName, edtConformpw;
    private TextInputLayout tilPassword, tilConformPassword, tilFirstName, tilLastName, tilUserName, tilEmail;
    private Button btnSignUp;

    private FirebaseAuth mAuth;

    private String email, password, firstName, conforPw, lastName,userName;
    private String uid;

    private ProgressDialog prgDialog;

    private DatabaseReference databaseUser;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        initControls();


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDataComplete()) {
                    signUp();
                }
            }
        });
    }

    private void initControls() {
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtsignEmail);
        edtPassword = findViewById(R.id.edtSingPassword);
        edtConformpw = findViewById(R.id.edtConformpassword);
        edtUserName = findViewById(R.id.edtUserName);

        tilPassword = findViewById(R.id.tilPassword);
        tilConformPassword = findViewById(R.id.tilConformPassword);
        tilFirstName = findViewById(R.id.tilFirstName);
        tilLastName = findViewById(R.id.tilLastName);
        tilEmail = findViewById(R.id.tilEmail);
        tilUserName = findViewById(R.id.tilUserName);

        btnSignUp = findViewById(R.id.btnSignUp);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        prgDialog = new ProgressDialog(signup.this);

        databaseUser = FirebaseDatabase.getInstance().getReference("User");
    }

    public void signUp() {

        prgDialog.setMessage("Registering User....");
        prgDialog.show();
        prgDialog.setCancelable(false);


        Log.d("email password", email + " " + password);
        if (mAuth == null) {
            Log.d("Auth", "Auth created");
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("inserted", "user Created");

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    prgDialog.dismiss();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(signup.this);
                    builder.setMessage(e.getLocalizedMessage())
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Error");
                    alert.show();
                    e.printStackTrace();
                    Log.e("not inserted", "user not Created", e);
                }
            });
        }
    }

    private void sendEmailVerfication() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            prgDialog.setMessage("Please Wait...");
            prgDialog.setCancelable(false);
            prgDialog.show();
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    prgDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(signup.this, "Check Your Email for Verification", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(signup.this, login.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    prgDialog.dismiss();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(signup.this);
                    builder.setMessage(e.getLocalizedMessage())
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Error");
                    alert.show();
                    e.printStackTrace();
                    Log.e("not inserted", "user not Created", e);
                }
            });

        }
    }

    private boolean isDataCompleteForUpdate() {
        email = edtEmail.getText().toString().trim();
        firstName = edtFirstName.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            edtEmail.setError("Please Enter Email");
            return false;
        } else if (TextUtils.isEmpty(firstName)) {
            edtFirstName.setError("Please Enter Name");
            return false;
        }
        return true;
    }

    private boolean isDataComplete() {
        email = edtEmail.getText().toString().trim();
        password = edtPassword.getText().toString().trim();
        firstName = edtFirstName.getText().toString().trim();
        lastName = edtLastName.getText().toString().trim();
        conforPw = edtConformpw.getText().toString().trim();
        userName = edtUserName.getText().toString().trim();


        if (TextUtils.isEmpty(firstName)) {
            tilFirstName.setError("Please Enter Name");
            edtFirstName.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(lastName)) {
            tilFirstName.setError(null);
            tilLastName.setError("Please Enter Name");
            edtLastName.requestFocus();
            return false;

        }  else if (TextUtils.isEmpty(userName)) {
            tilLastName.setError(null);
            tilUserName.setError("Please Enter User Name");
            edtUserName.requestFocus();
            return false;
        }else if (TextUtils.isEmpty(email) || !(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            tilLastName.setError(null);
            tilEmail.setError("Please Enter Email");
            edtEmail.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            tilEmail.setError(null);
            tilPassword.setError("Please Enter Password");
            edtPassword.requestFocus();
            return false;
        } else if (!conforPw.contentEquals(password)) {
            tilPassword.setError(null);
            tilConformPassword.setError("Password and confirm password must be same");
            edtConformpw.requestFocus();
            return false;
        } else {
            tilConformPassword.setError(null);
        }
        return true;
    }

    public void SaveData() {

        String id = mAuth.getCurrentUser().getUid();


        databaseUser.child(id).setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    /*startActivity(new Intent(SignupActivity.this, MainActivity.class));
                    finish();*/
                    sendEmailVerfication();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                prgDialog.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(signup.this);
                builder.setMessage(e.getLocalizedMessage())
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Error");
                alert.show();
                e.printStackTrace();
                Log.e("not inserted", "user not Created", e);
            }
        });
    }
}

