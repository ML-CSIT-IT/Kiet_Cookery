package com.koolz.kietcookery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Authentication extends AppCompatActivity {
    private TextInputLayout e1;
    private TextInputLayout e2;
    private TextInputEditText e21;
    private String email;
    private String password;
    private ProgressDialog progress;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String user_id;
    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        e1=(TextInputLayout) findViewById(R.id.email);
        e2=(TextInputLayout) findViewById(R.id.password);
        e21=(TextInputEditText) findViewById(R.id.password_ed);
        e21.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > e2.getCounterMaxLength())
                    e2.setError("Max character length is " + e2.getCounterMaxLength());
                else
                    e2.setError(null);
            }
        });
        auth = FirebaseAuth.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        progress = new ProgressDialog(this);
        progress.setMessage("Signing In...");
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);

    }

    public void login(View view){
        email = e1.getEditText().getText().toString();
        password = e2.getEditText().getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Empty Credentials", Toast.LENGTH_LONG).show();
        } else if (password.length() < 8) {
            Toast.makeText(this, "Password Must Be Of Length 8", Toast.LENGTH_LONG).show();
        }
        else{
            login_user(email, password);
            progress.show();
        }

    }
    private void login_user(final String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(Authentication.this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                user= FirebaseAuth.getInstance().getCurrentUser();
                if(user.isEmailVerified()){
                    user_id = user.getUid();
                    Toast.makeText(Authentication.this,"Login Sucessfully",Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child(user_id).child("Information");
                    dbref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            value= dataSnapshot.getValue(String.class);
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                    Intent intent;
                    if(value==null){
                        intent = new Intent(Authentication.this, Info.class);
                        Toast.makeText(Authentication.this,"Please add some important personal info..",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        intent = new Intent(Authentication.this, menu.class);
                    }
                    startActivity(intent);
                    finish();
                }
                else{
                    showCustomDialog_error();
                    progress.dismiss();
                }
            }
        });
        auth.signInWithEmailAndPassword(email, password).addOnFailureListener(Authentication.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Authentication.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }
    private void showCustomDialog_error() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.error_custom, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button bt= (Button) dialogView.findViewById(R.id.buttoncancel);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Toast.makeText(Authentication.this,"Email Not Verified",Toast.LENGTH_SHORT).show();
                e1.getEditText().setText(null);
                e2.getEditText().setText(null);
                FirebaseAuth.getInstance().signOut();
            }
        });
        Button bt2= (Button) dialogView.findViewById(R.id.buttonsent);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                user.sendEmailVerification();
                showCustomDialog_tick();
            }
        });
    }
    private void showCustomDialog_tick() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.tick_custom, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        TextView tv=(TextView)dialogView.findViewById(R.id.customtv);
        tv.setText(getResources().getString(R.string.verification_email_sent_successfully));
        TextView tv2=(TextView)dialogView.findViewById(R.id.customtv1);
        tv2.setText(getResources().getString(R.string.verify_email_and_then_login_again));
        Button bt = (Button) dialogView.findViewById(R.id.buttonOk);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                e1.getEditText().setText(null);
                e2.getEditText().setText(null);
                FirebaseAuth.getInstance().signOut();
            }
        });
        Button bt1 = (Button) dialogView.findViewById(R.id.buttonsent);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                user.sendEmailVerification();
                showCustomDialog_tick();
            }
        });
    }

    public void Register(View view) {
        Intent intent = new Intent(this,register.class);
        startActivity(intent);
    }

    public void forgot_password(View view) {
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (user != null) {
            Intent intent = new Intent(this,Info.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent6 = new Intent(Intent.ACTION_MAIN);
        intent6.addCategory(Intent.CATEGORY_HOME);
        intent6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent6);
    }
}