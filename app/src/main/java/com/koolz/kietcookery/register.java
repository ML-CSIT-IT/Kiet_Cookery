package com.koolz.kietcookery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class register extends AppCompatActivity {
    private TextInputLayout e1;
    private TextInputLayout e2;
    private TextInputLayout e3;
    private TextInputEditText e31;
    private TextInputLayout e4;
    private TextInputEditText e41;
    private Uri picUri;
    private ImageView iv;
    private UploadTask uploadTask;
    private ProgressDialog progress;
    private Bitmap bt;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int IMAGE_RESULT = 200;
    private String username;
    private ByteArrayOutputStream baos;
    private byte[] data1;
    private String email;
    private String password;
    private String cpassword;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e1=(TextInputLayout) findViewById(R.id.user);
        e2=(TextInputLayout) findViewById(R.id.email);
        e3=(TextInputLayout) findViewById(R.id.password);
        e4=(TextInputLayout) findViewById(R.id.cpassword);
        e31=(TextInputEditText) findViewById(R.id.password_ed);
        e31.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > e3.getCounterMaxLength())
                    e3.setError("Max character length is " + e3.getCounterMaxLength());
                else
                    e3.setError(null);
            }
        });
        e41=(TextInputEditText) findViewById(R.id.cpassword_ed);
        e41.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > e4.getCounterMaxLength())
                    e4.setError("Max character length is " + e4.getCounterMaxLength());
                else
                    e4.setError(null);
            }
        });
        iv=(ImageView)findViewById(R.id.profile_image);
        auth=FirebaseAuth.getInstance();
        progress = new ProgressDialog(this);
        progress.setMessage("Registering...");
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
        baos = new ByteArrayOutputStream();
        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }
    public void Register(View view) {
        username = e1.getEditText().getText().toString();
        email = e2.getEditText().getText().toString();
        password = e3.getEditText().getText().toString();
        cpassword= e4.getEditText().getText().toString();
        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(username)||TextUtils.isEmpty(cpassword)){
            Toast.makeText(this,"Empty Credentials",Toast.LENGTH_LONG).show();
        }
        else if (password.length()<8){
            Toast.makeText(this,"Password Must Be Of Length 8",Toast.LENGTH_LONG).show();
        }
        else if(!cpassword.equals(password)){
            e4.setError("Confirm Password and Password does not match");
        }
        else if(data1==null){
            Toast.makeText(this,"Please Select Outlet Image",Toast.LENGTH_LONG).show();
        }
        else {
            register_user(email,password);
            progress.show();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private void register_user(final String email, String password) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user= FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user_id = user.getUid();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username).build();
                        user.updateProfile(profileUpdates);
                        if (data1 != null) {
                            StorageReference stref = FirebaseStorage.getInstance().getReference().child("Outlet Image").child(user_id).child(username);
                            FirebaseDatabase.getInstance().getReference().child(user_id).child(username).child("Information").setValue("1");
                            uploadTask = (UploadTask) stref.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    user.sendEmailVerification().addOnSuccessListener(register.this,
                                            new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progress.dismiss();
                                                    showCustomDialog_tick();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(register.this,"Verification Email Sent failed",Toast.LENGTH_SHORT).show();
                                            progress.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
                else {
                    try
                    {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException weakPassword)
                    {
                        Toast.makeText(register.this,"Weak Password",Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                    catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                    {
                        Toast.makeText(register.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                    catch (FirebaseAuthUserCollisionException existEmail)
                    {
                        Toast.makeText(register.this,"Email Already Exists"+"\nOr Login to Verify Previous Registered Email",Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
                    catch (Exception e) {
                    }
                }
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
                Intent intent = new Intent(register.this,Authentication.class);
                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
                finish();
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
    public void Select(View view) {
        startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
    }
    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        return chooserIntent;
    }
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_RESULT) {

                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    try {
                        bt = BitmapFactory.decodeFile(filePath);
                        iv.setImageBitmap(bt);
                        bt.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                        data1 = baos.toByteArray();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;
        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());
    }
    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }
    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("pic_uri", picUri);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        picUri = savedInstanceState.getParcelable("pic_uri");
    }
    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }
}