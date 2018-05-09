package com.igorronner.irloginbackup.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.igorronner.irloginbackup.R;
import com.igorronner.irloginbackup.init.ConfigUtil;
import com.igorronner.irloginbackup.models.FirebaseBackup;
import com.igorronner.irloginbackup.models.FirebaseUser;
import com.igorronner.irloginbackup.preferences.FirebasePreference;
import com.igorronner.irloginbackup.services.FirebaseAuthService;
import com.igorronner.irloginbackup.services.FirebaseDatabaseService;
import com.igorronner.irloginbackup.services.FirebaseStorageService;
import com.igorronner.irloginbackup.utils.BackupAndRestore;
import com.igorronner.irloginbackup.utils.ConnectionUtil;
import com.igorronner.irloginbackup.utils.MD5GenratorUtils;
import com.igorronner.irloginbackup.utils.PermissionsUtils;

import java.io.File;

public class SignInActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener{

    private EditText editEmail;
    private EditText editPassword;
    private Button signIn;
    private TextView signUp;
    private Button signInGoogle;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private static final String TAG = SignInActivity.class.getName();
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setStatusBarColor(ConfigUtil.COLOR_PRIMARY_DARK);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        signIn = (Button) findViewById(R.id.signIn);
        signInGoogle = (Button) findViewById(R.id.signInGoogle);
        signUp = (TextView) findViewById(R.id.signUp);
        scrollView = findViewById(R.id.scrollView);
        linearLayout = findViewById(R.id.linearLayout);

        ImageView logo = findViewById(R.id.logo);
        if (ConfigUtil.LOGO > 0)
            logo.setImageResource(ConfigUtil.LOGO);
        else
            logo.setVisibility(View.GONE);

        scrollView.setBackgroundColor(ContextCompat.getColor(this, ConfigUtil.COLOR_PRIMARY));
        signIn.setTextColor(ContextCompat.getColor(this, ConfigUtil.COLOR_PRIMARY));

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ConnectionUtil.isConnected(SignInActivity.this)){
                    Toast.makeText(SignInActivity.this, R.string.need_internet, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (validateInputsEmpty()) {
                    Toast.makeText(SignInActivity.this, R.string.fill_required_fields, Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
                String password = MD5GenratorUtils.getMD5(editPassword.getText().toString());
                FirebaseAuthService
                        .getInstance(SignInActivity.this)
                        .signWithEmailAndPassword(editEmail.getText().toString(), password, new FirebaseAuthService.ServiceListener() {
                            @Override
                            public void onAuthComplete() {

                                progressDialog.dismiss();
                                updateBackup();
                            }

                            @Override
                            public void onError(String error) {
                                progressDialog.dismiss();
                                Toast.makeText(SignInActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ConnectionUtil.isConnected(SignInActivity.this)){
                    Toast.makeText(SignInActivity.this, R.string.need_internet, Toast.LENGTH_SHORT).show();
                    return;
                }

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("708337287617-7kbus9cimuh7v8in1hqq5mvrl8qoskpe.apps.googleusercontent.com")
                        .requestEmail()
                        .build();

                if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
                    try {
                        mGoogleApiClient = new GoogleApiClient.Builder(SignInActivity.this)
                                .enableAutoManage(SignInActivity.this /* FragmentActivity */, SignInActivity.this /* OnConnectionFailedListener */)
                                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                                .build();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mAuth = FirebaseAuth.getInstance();
                signIn();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });


    }


    private void updateBackup(){
        if (PermissionsUtils.isStoragePermissionGranted(SignInActivity.this)) {
            final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
            progressDialog.setMessage(getString(R.string.sync));
            progressDialog.setCancelable(false);
            progressDialog.show();
            FirebaseDatabaseService
                    .getInstance(this)
                    .getBackup(new FirebaseDatabaseService.ServiceListener<FirebaseBackup>() {
                        @Override
                        public void onComplete(FirebaseBackup result) {
                            if (result == null){
                                progressDialog.dismiss();
                                finish();
                                return;
                            }

                            FirebaseStorageService
                                    .getInstance(SignInActivity.this)
                                    .downloadBackupFile(result.getFile_path(), new FirebaseStorageService.DownloadServiceListener<File>() {
                                        @Override
                                        public void onDownloadComplete(File file) {
                                            BackupAndRestore.importDB(SignInActivity.this);
                                            progressDialog.dismiss();
                                            finish();

                                        }
                                    });

                        }
                    });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            updateBackup();
        } else {
            FirebasePreference.getInstance().setUuid(SignInActivity.this, null);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }



    private boolean validateInputsEmpty(){
        return editEmail.getText().toString().isEmpty() ||
                editPassword.getText().toString().isEmpty();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient!=null) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(this, R.string.error_login, Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = new FirebaseUser();
                        user.setUuid(authResult.getUser().getUid());
                        user.setEmail(authResult.getUser().getEmail());
                        user.setName(authResult.getUser().getDisplayName());
                        FirebaseDatabaseService
                                .getInstance(SignInActivity.this)
                                .saveUser(user, new FirebaseDatabaseService.ServiceListener<FirebaseUser>() {
                                    @Override
                                    public void onComplete(FirebaseUser result) {
                                        progressDialog.dismiss();
                                        FirebasePreference.getInstance().setUuid(SignInActivity.this, result.getUuid());
                                        updateBackup();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignInActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAffinity(this);
    }
}