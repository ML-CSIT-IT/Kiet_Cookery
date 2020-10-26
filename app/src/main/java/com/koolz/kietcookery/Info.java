package com.koolz.kietcookery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Info extends AppCompatActivity {
    private String Account_no;
    private String Ifsc_code;
    private String C_Account_no;
    private String Holder_name;
    private String Manager_name;
    private String Phone_no;
    private String Adhaar_no;
    private String username;
    private String user_id;
    private DatabaseReference dbref;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        register Register = new register();
        username = Register.getUsername();
        user= FirebaseAuth.getInstance().getCurrentUser();
        dbref= FirebaseDatabase.getInstance().getReference().child(user_id).child(username).child("Information");
    }

    public void Go(View view) {

    }
}