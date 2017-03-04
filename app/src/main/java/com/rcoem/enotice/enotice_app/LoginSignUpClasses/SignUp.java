package com.rcoem.enotice.enotice_app.LoginSignUpClasses;

/**
 * Created by Zubin Paul on 29-10-2016.
 */


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Patterns;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rcoem.enotice.enotice_app.Animations.LoginLoadingView;
import com.rcoem.enotice.enotice_app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Handler;

public class SignUp extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener  {

    private FrameLayout mFrtContent;

    private Scene mSceneSignUp;
    private Scene mSceneLogging;
    private Scene mSceneMain;

    private int mTvSighUpWidth, mTvSighUpHeight;
    private int mDuration;

    private EditText mName;
    private EditText emailtext;
    private EditText passwordtext;
    private EditText repasswordtext;
    private RelativeLayout li;
    private RadioGroup designation;
    public Context signupcontext;

    public String departmentSelected;
    public String email;
    public String password;
    public String repassword;

    private Spinner dept;

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    //defining FireBase Auth object
    public FirebaseAuth firebaseAuth;

    //defining FireBase real-time database object
    public DatabaseReference mDatabase1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupcontext = this;

        mFrtContent = (FrameLayout) findViewById(R.id.frt_content);

        mDuration = 500;

        mSceneSignUp = Scene.getSceneForLayout(mFrtContent, R.layout.scene_sign_up, this);
        mSceneSignUp.setEnterAction(new Runnable() {
            @Override
            public void run() {
                final LoginLoadingView loginView = (LoginLoadingView) mFrtContent.findViewById(R.id.login_view_signup);
                ViewTreeObserver vto = loginView.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        setSize(loginView.getMeasuredWidth(), loginView.getMeasuredHeight());
                    }
                });
                loginView.setOnClickListener(SignUp.this);

                dept = (Spinner) findViewById(R.id.spinner);
                //dept.setOnItemSelectedListener(this);

                List<String> categories = new ArrayList<String>();
                categories.add("Select a Department");
                categories.add("Computer Science");
                categories.add("Mechanical");

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, categories);

                // Drop down layout style - list view with radio button
                // dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                dept.setAdapter(dataAdapter);

            }
        });


        mSceneLogging = Scene.getSceneForLayout(mFrtContent, R.layout.scene_logging, this);
        mSceneLogging.setEnterAction(new Runnable() {
            @Override
            public void run() {
                final LoginLoadingView loginView = (LoginLoadingView) mFrtContent.findViewById(R.id.login_view);
                loginView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginView.setStatus(LoginLoadingView.STATUS_LOGGING);
                    }
                }, mDuration);

                loginView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginView.setStatus(LoginLoadingView.STATUS_LOGIN_SUCCESS);
                    }
                }, 6000);

                loginView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TransitionManager.go(mSceneMain, new ChangeBounds().setDuration(mDuration).setInterpolator(new DecelerateInterpolator()));
                    }
                }, 8000);


            }
        });

        mSceneMain = Scene.getSceneForLayout(mFrtContent, R.layout.scene_main, this);
        mSceneMain.setEnterAction(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofInt(0, 500);
                animator.setDuration(mDuration);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int alpha = (int) animation.getAnimatedValue();
                    }
                });
                animator.start();

                //final RecyclerView recyclerView = (RecyclerView) mFrtContent.findViewById(R.id.rv_common);
                //CommonAdapter adapter = new CommonAdapter(MainActivity.this);
                //recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                //recyclerView.setAdapter(adapter);
                startActivity(new Intent(SignUp.this,MainActivity.class));
                finish();

            }
        });
        TransitionManager.go(mSceneSignUp);
    }

    private void setSize(int width, int height) {
        mTvSighUpWidth = width;
        mTvSighUpHeight = height;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        departmentSelected = parent.getItemAtPosition(position).toString();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(final View view) {

        boolean signUpOk = false;
        boolean nameAllGood = false;
        boolean emailAllGood = false;
        boolean passwordAllGood = false;
        boolean deptAllGood = false;
        boolean desgAllGood = false;
        boolean network = false;


        mName = (EditText) findViewById(R.id.editName);
        emailtext = (EditText) findViewById(R.id.sign_up_email_text);
        passwordtext = (EditText) findViewById(R.id.sign_up_password_text);
        repasswordtext = (EditText) findViewById(R.id.sign_up_repassword_text);
        li = (RelativeLayout) findViewById(R.id.sign_up_relative);

        designation = (RadioGroup) findViewById(R.id.desg_rdio_btn);
        final String userdept;
        RadioButton userdesgrdiobuttn;

        final String Name = mName.getText().toString().trim();

        if(TextUtils.isEmpty(Name))
        {
            /*Snackbar snackbar = Snackbar
                    .make(li, R.string.empty_field, Snackbar.LENGTH_LONG);
            snackbar.show();*/
            Toast.makeText(SignUp.this, R.string.empty_field, Toast.LENGTH_LONG).show();
        }
        else {
            nameAllGood = true;
        }


        //Validate Email
        email = emailtext.getText().toString().trim();
        if(!isValidEmail(email)){
            emailtext.setError("Enter a valid Email Address.");
            /*Snackbar snackbar = Snackbar
                    .make(li, R.string.empty_field, Snackbar.LENGTH_LONG);
            snackbar.show();*/
            Toast.makeText(this, R.string.incorrect_email, Toast.LENGTH_LONG).show();
        }else {
            emailAllGood = true;
        }

        //Validate Password
        password = passwordtext.getText().toString();
        repassword = repasswordtext.getText().toString();
        if(password.length() < 8){
            passwordtext.setError("Must be atleast 8 characters long.");
            /*Snackbar snackbar = Snackbar
                    .make(li, R.string.incorrect_pass_length, Snackbar.LENGTH_LONG);
            snackbar.show();*/
            Toast.makeText(this, R.string.incorrect_pass_length, Toast.LENGTH_LONG).show();
        }
        else if(password.length() >= 8){
            if(!password.equals(repassword)){
                passwordtext.setError("Passwords do not match.");
                /*Snackbar snackbar = Snackbar
                        .make(li, "Passwords do not match.", Snackbar.LENGTH_LONG);
                snackbar.show();*/
                Toast.makeText(this, "Password do not match.", Toast.LENGTH_LONG).show();
            }
            else{
                passwordAllGood = true;
            }
        }


        //Validate department

        departmentSelected = dept.getSelectedItem().toString();

        if(departmentSelected.equals("Select a Department")){
            /*Snackbar snackbar = Snackbar
                    .make(li, "Please select a Department", Snackbar.LENGTH_LONG);
            snackbar.show();*/
            Toast.makeText(this, "Please select a Department", Toast.LENGTH_LONG).show();
        }else{
            deptAllGood = true;
        }

        //Validate designation
        int desgSelectedId = designation.getCheckedRadioButtonId();
        userdesgrdiobuttn = (RadioButton) findViewById(desgSelectedId);
        userdept = userdesgrdiobuttn.getText().toString();
        Toast.makeText(SignUp.this, userdept, Toast.LENGTH_LONG).show();
        if(designation.getCheckedRadioButtonId() == -1) {
            /*Snackbar snackbar = Snackbar
                    .make(li, "Please select your Designation", Snackbar.LENGTH_LONG);
            snackbar.show();*/
            Toast.makeText(this, "Please select a Designation", Toast.LENGTH_LONG).show();
        } else {
            desgAllGood = true;
        }

        //Check Internet Connection
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();
        if(nInfo!=null && nInfo.isConnected()) {
            network = true;
        }

        //Complete SignUpAction
        if(nameAllGood && emailAllGood && passwordAllGood && deptAllGood && desgAllGood){
            if(network) {
                signUpOk = true;
            }
            else {
                Toast.makeText(this, "Please Connect to the Internet.", Toast.LENGTH_LONG).show();
            }
        }


        if(signUpOk) {
            float finalRadius = (float) Math.hypot(mFrtContent.getWidth(), mFrtContent.getHeight());

            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];

            Animator anim = ViewAnimationUtils.createCircularReveal(mFrtContent, x + mTvSighUpWidth / 2, y - mTvSighUpHeight / 2, 100, finalRadius);
            mFrtContent.setBackgroundColor(ContextCompat.getColor(SignUp.this, R.color.colorBg));
            anim.setDuration(mDuration);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mFrtContent.setBackgroundColor(Color.TRANSPARENT);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            anim.start();

            //Initializing FireBase Auth object
            firebaseAuth = FirebaseAuth.getInstance();

            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            //creating a new user in FireBase Auth Database
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //checking if success
                            if(task.isSuccessful()){
                                //display some message here
                            }else{
                                //display some message here
                                /*Snackbar snackbar = Snackbar
                                        .make(li, R.string.reg_fail, Snackbar.LENGTH_LONG);
                                snackbar.show();*/
                                Toast.makeText(SignUp.this, R.string.reg_fail, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        profileSetup(userdept);
                    }
                    catch (Exception e) {
                        Toast.makeText(SignUp.this,"",Toast.LENGTH_LONG).show();
                    }
                }
            }, 4500);



            TransitionManager.go(mSceneLogging, new ChangeBounds().setDuration(mDuration).setInterpolator(new DecelerateInterpolator()));
        }
    }

    public void profileSetup(String userdept) {
        String userdept_firebase;
        String userdesg;
        int userdesg_firebase;

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH) + 1;   //Month in Calendar API start with 0.
        day = calendar.get(Calendar.DAY_OF_MONTH);
        Toast.makeText(SignUp.this,day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
        final String currentDate = day + "/" + month + "/" + year;

        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users");

        final String Name = mName.getText().toString().trim();
        final String user_id = firebaseAuth.getCurrentUser().getUid();
        mDatabase1.child(user_id).child("name").setValue(Name);

        mDatabase1.child(user_id).child("block").setValue("No");

        mDatabase1.child(user_id).child("images").setValue("https://firebasestorage.googleapis.com/v0/b/e-notice-board-83d16.appspot.com/o/ic_launcher.png?alt=media&token=2753442a-4c8d-45a3-a6c5-0bf4257c2cc7");

        //Validate designation

        //Toast.makeText(SignUp.this,userdept,Toast.LENGTH_LONG).show();
        if(userdept.equals("HOD")) {
            userdesg_firebase = 99;
            mDatabase1.child(user_id).child("DEST").setValue("HOD");
            //Toast.makeText(SignUp.this,userdesg_firebase,Toast.LENGTH_LONG).show();
            mDatabase1.child(user_id).child("level").setValue(userdesg_firebase);
        }
        else{
            userdesg_firebase = 99;
            mDatabase1.child(user_id).child("DEST").setValue("AP");
            //Toast.makeText(SignUp.this,userdesg_firebase,Toast.LENGTH_LONG).show();
            mDatabase1.child(user_id).child("level").setValue(userdesg_firebase);
        }

        departmentSelected = dept.getSelectedItem().toString();
        //Validate department
        if(departmentSelected.equals("Computer Science")) {
            userdept_firebase = "CSE";
            Toast.makeText(SignUp.this,userdept_firebase,Toast.LENGTH_LONG).show();
            mDatabase1.child(user_id).child("department").setValue(userdept_firebase);
        }
        else {
            userdept_firebase = "Mech";
            Toast.makeText(SignUp.this,userdept_firebase,Toast.LENGTH_LONG).show();
            mDatabase1.child(user_id).child("department").setValue(userdept_firebase);
        }

        mDatabase1.child(user_id).child("time").setValue(currentDate);


    }

    public final static boolean isValidEmail(CharSequence target){
        if(TextUtils.isEmpty(target)){
            return false;
        }
        else{
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
