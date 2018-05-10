package com.igorronner.irloginbackup.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.igorronner.irloginbackup.R;
import com.igorronner.irloginbackup.init.ConfigUtil;
import com.igorronner.irloginbackup.models.FirebaseUser;
import com.igorronner.irloginbackup.services.FirebaseAuthService;
import com.igorronner.irloginbackup.utils.ConnectionUtil;

public class SignUpActivity extends BaseActivity {

    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editPasswordConfirm;
    private Button signUp;
    private ScrollView scrollView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setStatusBarColor(ConfigUtil.COLOR_PRIMARY_DARK);

        editName = (EditText) findViewById(R.id.editName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editPasswordConfirm = (EditText) findViewById(R.id.editPasswordConfirm);
        signUp = (Button) findViewById(R.id.signUp);
        scrollView = findViewById(R.id.scrollView);
        linearLayout = findViewById(R.id.linearLayout);

        scrollView.setBackgroundColor(ContextCompat.getColor(this, ConfigUtil.COLOR_PRIMARY));
        signUp.setTextColor(ContextCompat.getColor(this, ConfigUtil.COLOR_PRIMARY));

        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(ConfigUtil.LOGO);
        if (ConfigUtil.LOGO > 0)
            logo.setImageResource(ConfigUtil.LOGO);
        else
            logo.setVisibility(View.GONE);

        TextView alreadyAccount = findViewById(R.id.alreadyAccount);
        alreadyAccount.setTextColor(ContextCompat.getColor(this, ConfigUtil.COLOR_ACCENT));

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputsEmpty()){
                    Toast.makeText(SignUpActivity.this, R.string.fill_required_fields, Toast.LENGTH_SHORT).show();
                } else if (validatePasswordSize()){
                    Toast.makeText(SignUpActivity.this, R.string.validate_password_size, Toast.LENGTH_SHORT).show();
                } else if (validatePasswordMatch()){
                    Toast.makeText(SignUpActivity.this, R.string.validate_password_match, Toast.LENGTH_SHORT).show();
                } else if (!ConnectionUtil.isConnected(SignUpActivity.this)){
                    Toast.makeText(SignUpActivity.this, R.string.need_internet, Toast.LENGTH_SHORT).show();
                } else
                    register();
            }
        });

        findViewById(R.id.signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });


    }

    private boolean validateInputsEmpty(){
        return editName.getText().toString().isEmpty() ||
                editEmail.getText().toString().isEmpty() ||
                editPassword.getText().toString().isEmpty() ||
                editPasswordConfirm.getText().toString().isEmpty() ;
    }

    private boolean validatePasswordMatch(){
        return !editPassword.getText().toString().equals(editPasswordConfirm.getText().toString());
    }

    private boolean validatePasswordSize(){
        return editPassword.getText().toString().length() < 4 || editPasswordConfirm.getText().toString().length() < 4;
    }

    private void register(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseUser user = new FirebaseUser();
        user.setEmail(editEmail.getText().toString());
        user.setName(editName.getText().toString());
        user.setPassword(editPassword.getText().toString());

        FirebaseAuthService.getInstance(this).register(user, new FirebaseAuthService.ServiceListener() {
            @Override
            public void onAuthComplete() {
                progressDialog.dismiss();
                finish();

            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAffinity(this);
    }
}
