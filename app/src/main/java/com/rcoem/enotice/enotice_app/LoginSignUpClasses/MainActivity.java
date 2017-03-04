package com.rcoem.enotice.enotice_app.LoginSignUpClasses;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ldoublem.loadingviewlib.view.LVBlock;
import com.rcoem.enotice.enotice_app.AdminClasses.AccountActivityAdmin;
import com.rcoem.enotice.enotice_app.AuthorityClasses.AccountAdminPanel;
import com.rcoem.enotice.enotice_app.HighAuthorityClasses.AccountActivityAuthority;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.UserClasses.AccountActivityUser;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;

    private View mLoginBtn;
    private TextView mSignupBtn;
    private TextView forgotPass;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public boolean isFirstStart;

    private ProgressDialog progressDialog;

    private DatabaseReference mDatabase;

    private int backButtonCount = 0;
    private ProgressDialog mProgress;

    LinearLayout li;
    long back_pressed;

    private LVBlock mLVBlock;
    private static int ANIMATION_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        li = (LinearLayout) findViewById(R.id.lay123);

        mAuth = FirebaseAuth.getInstance();

        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);

        mLoginBtn = (View) findViewById(R.id.loginBtn);
        mSignupBtn = (TextView) findViewById(R.id.signupBtn);
        mProgress  = new ProgressDialog(this);
        forgotPass = (TextView) findViewById(R.id.forgotPass);
        mProgress.setCancelable(false);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null) {
                    //User has logged-in already
                    //Move user directly to the Account Activity.
                    setContentView(R.layout.activity_splash);
                    mLVBlock = (LVBlock) findViewById(R.id.lv_block);

                    mLVBlock.setViewColor(Color.rgb(245,209,22));
                    mLVBlock.setShadowColor(Color.GRAY);
                    mLVBlock.startAnim(1000);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // This method will be executed once the timer is over
                            // Start your app main activity
                            mLVBlock.stopAnim();
                            // close this activity

                    //mProgress.setMessage("Give Us A Moment...");
                    //mProgress.show();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final String string = dataSnapshot.child("level").getValue().toString().trim();
                            switch (string) {
                                case "4": {
                                    mProgress.dismiss();
                                    Intent intent = new Intent(MainActivity.this, AccountAdminPanel.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }
                                case "3": {
                                    mProgress.dismiss();
                                    Intent intent = new Intent(MainActivity.this, AccountActivityAuthority.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }
                                case "99": {
                                    mProgress.dismiss();
                                    Intent intent = new Intent(MainActivity.this, AwatingForApproval.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }
                                case "2": {
                                    mProgress.dismiss();
                                    Intent intent = new Intent(MainActivity.this, AccountActivityAdmin.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }
                                default: {
                                    mProgress.dismiss();
                                    Intent intent = getIntent();
                                    final String fBack = intent.getStringExtra("fBack");

                                    if (intent.getStringExtra("fBack") != null) {
                                        Toasty.info(MainActivity.this, fBack, Toast.LENGTH_LONG, true).show();
                                    } else {
                                        //Do Nothing
                                    }
                                    intent = new Intent(MainActivity.this, AccountActivityUser.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                        }
                    }, ANIMATION_DELAY);  //Delay after which the inside code gets executed
                }
            }
        };

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSignIn();

            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PasswordResetActivity.class));
            }
        });

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });

        progressDialog = new ProgressDialog(MainActivity.this);

        // Checking for first time launch - before calling setContentView()
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, MainIntroActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

// Start the thread
        t.start();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

   /* @Override
    public void onClick(final View view) {

        startSignIn();

    }*/

    private void startSignIn() {

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            //User did not enter any email or password.
            Snackbar snackbar = Snackbar
                    .make(li, R.string.empty_field, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar snackbar = Snackbar
                    .make(li, R.string.incorrect_email, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else {

            progressDialog.setMessage("Logging in...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        //if incorrect email/password entered.
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(li, R.string.auth_failed, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(li, R.string.auth_success, Snackbar.LENGTH_LONG);
                        snackbar.show();

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                    final String string = dataSnapshot.child("level").getValue().toString().trim();

                                    if(string.equals("4")){
                                        //  mProgress.dismiss();
                                        Intent intent = new Intent(MainActivity.this, AccountAdminPanel.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else if(string.equals("99")){
                                        // mProgress.dismiss();
                                        Intent intent = new Intent(MainActivity.this, AwatingForApproval.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else if (string.equals("2")) {
                                        //  mProgress.dismiss();
                                        Intent intent = new Intent(MainActivity.this, AccountActivityAdmin.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        //mProgress.dismiss();
                                        Intent intent = new Intent(MainActivity.this, AccountActivityUser.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }
            });
        }

    }

    /**
     * Back button listener.
     * Will close the application if the back button pressed twice.
     */
    @Override
    public void onBackPressed()
    {
        if (back_pressed + 3000 > System.currentTimeMillis()){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else{
            back_pressed = System.currentTimeMillis();
            Snackbar snackbar = Snackbar
                    .make(li, R.string.backpress, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}
