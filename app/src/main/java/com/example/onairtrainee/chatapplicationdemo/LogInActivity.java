package com.example.onairtrainee.chatapplicationdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class LogInActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;

    private Button LogInButton;
    private EditText LogInEmail;
    private EditText LogInPassword;

    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();


        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LogInButton = (Button) findViewById(R.id.login_button);
        LogInEmail = (EditText) findViewById(R.id.login_email);
        LogInPassword = (EditText) findViewById(R.id.login_password);
        loadingBar = new ProgressDialog(this);


        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = LogInEmail.getText().toString();
                String password = LogInPassword.getText().toString();
                
                LogInUserAccount(email,password);
            }
        });

    }

    private void LogInUserAccount(String email,String password)
    {
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(LogInActivity.this,"Please Enter Your Email",Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(LogInActivity.this,"Please Enter Your Password",Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("LogIn Account");
            loadingBar.setMessage("Please Wait, While Logging in Your Account...");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                String online_user_id = mAuth.getCurrentUser().getUid();

                                Intent mainIntent = new Intent(LogInActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();



                            }
                            else
                            {
                                Toast.makeText(LogInActivity.this,"Wrong Email or Password, Check And Try Again....",Toast.LENGTH_SHORT).show();
                            }

                            loadingBar.dismiss();
                        }
                    });
        }
    }
}
