package com.rcoem.enotice.enotice_app.LoginSignUpClasses;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.rcoem.enotice.enotice_app.AdminClasses.AccountActivityAdmin;
import com.rcoem.enotice.enotice_app.AuthorityClasses.AccountAdminPanel;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.UserClasses.AccountActivityUser;

public class LoginFragment extends SlideFragment {

    Context context;
    LinearLayout li;

    private EditText mEmailField;
    private EditText mPasswordField;

    private View mLoginBtn;
    private TextView mSignupBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private ProgressDialog progressDialog;

    private boolean loggedIn = false;

    private Handler loginHandler = new Handler();

    private Runnable loginRunnable = new Runnable() {
        @Override
        public void run() {
            //mLoginBtn.setText(R.string.label_fake_login_success);
            //Toast.makeText(getContext(), R.string.label_fake_login_success_message, Toast.LENGTH_SHORT).show();


            loggedIn = true;
            updateNavigation();
        }
    };

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        li = (LinearLayout) root.findViewById(R.id.lay123);

        mEmailField = (EditText) root.findViewById(R.id.emailField);
        mPasswordField = (EditText) root.findViewById(R.id.passwordField);
        mLoginBtn = (View) root.findViewById(R.id.loginBtn);
        mSignupBtn = (TextView) root.findViewById(R.id.signupBtn);

        mEmailField.setEnabled(!loggedIn);
        mPasswordField.setEnabled(!loggedIn);
        mLoginBtn.setEnabled(!loggedIn);

        progressDialog = new ProgressDialog(getActivity());

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    //User has logged-in already
                    //Move user directly to the Account Activity.
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final String string = dataSnapshot.child("level").getValue().toString().trim();
                            switch (string) {
                                case "4": {
                                    Intent intent = new Intent(getActivity(), AccountAdminPanel.class);
                                    startActivity(intent);
                                    // finish();
                                    break;
                                }
                                case "99": {
                                    Intent intent = new Intent(getActivity(), AwatingForApproval.class);
                                    startActivity(intent);
                                    //finish();
                                    break;
                                }
                                case "2": {
                                    Intent intent = new Intent(getActivity(), AccountActivityAdmin.class);
                                    startActivity(intent);
                                    //finish();
                                    break;
                                }
                                default: {
                                    Intent intent = new Intent(getActivity(), AccountActivityUser.class);
                                    startActivity(intent);
                                    //finish();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSignIn();
                // fakeLogin.setText(R.string.label_fake_login_loading);

                // loginHandler.postDelayed(loginRunnable, 2000);
            }
        });

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), SignUp.class));
            }
        });

        return root;
    }

    public void onStart() {
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

    private void startSignIn() {

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            //User did not enter any email or password.
            Snackbar snackbar = Snackbar
                    .make(li, R.string.empty_field, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {

            progressDialog.setMessage("Logging in...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        //if incorrect email/password entered.
                        Snackbar snackbar = Snackbar
                                .make(li, R.string.auth_failed, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        progressDialog.dismiss();
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
                                    Intent intent = new Intent(getActivity(), AccountAdminPanel.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                                else if(string.equals("99")){
                                    // mProgress.dismiss();
                                    Intent intent = new Intent(getActivity(), AwatingForApproval.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                                else if (string.equals("2")) {
                                    //  mProgress.dismiss();
                                    Intent intent = new Intent(getActivity(), AccountActivityAdmin.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    //mProgress.dismiss();
                                    Intent intent = new Intent(getActivity(), AccountActivityUser.class);
                                    startActivity(intent);
                                    getActivity().finish();
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

    @Override
    public void onDestroy() {
        loginHandler.removeCallbacks(loginRunnable);
        super.onDestroy();
    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }

}