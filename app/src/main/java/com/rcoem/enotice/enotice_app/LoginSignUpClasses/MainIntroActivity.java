package com.rcoem.enotice.enotice_app.LoginSignUpClasses;

/**
 * Created by Akshat Shukla on 22-10-2016.
 */


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.app.OnNavigationBlockedListener;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.heinrichreimersoftware.materialintro.slide.Slide;
import com.rcoem.enotice.enotice_app.AdminClasses.AccountActivityAdmin;
import com.rcoem.enotice.enotice_app.AuthorityClasses.AccountAdminPanel;
import com.rcoem.enotice.enotice_app.HighAuthorityClasses.AccountActivityAuthority;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.UserClasses.AccountActivityUser;

public class MainIntroActivity extends IntroActivity {

    public static final String EXTRA_FULLSCREEN = "com.heinrichreimersoftware.materialintro.demo.EXTRA_FULLSCREEN";
    public static final String EXTRA_SCROLLABLE = "com.heinrichreimersoftware.materialintro.demo.EXTRA_SCROLLABLE";
    public static final String EXTRA_CUSTOM_FRAGMENTS = "com.heinrichreimersoftware.materialintro.demo.EXTRA_CUSTOM_FRAGMENTS";
    public static final String EXTRA_PERMISSIONS = "com.heinrichreimersoftware.materialintro.demo.EXTRA_PERMISSIONS";
    public static final String EXTRA_SHOW_BACK = "com.heinrichreimersoftware.materialintro.demo.EXTRA_SHOW_BACK";
    public static final String EXTRA_SHOW_NEXT = "com.heinrichreimersoftware.materialintro.demo.EXTRA_SHOW_NEXT";
    public static final String EXTRA_SKIP_ENABLED = "com.heinrichreimersoftware.materialintro.demo.EXTRA_SKIP_ENABLED";
    public static final String EXTRA_FINISH_ENABLED = "com.heinrichreimersoftware.materialintro.demo.EXTRA_FINISH_ENABLED";
    public static final String EXTRA_GET_STARTED_ENABLED = "com.heinrichreimersoftware.materialintro.demo.EXTRA_GET_STARTED_ENABLED";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        boolean fullscreen = intent.getBooleanExtra(EXTRA_FULLSCREEN, false);
        boolean scrollable = intent.getBooleanExtra(EXTRA_SCROLLABLE, false);
        boolean customFragments = intent.getBooleanExtra(EXTRA_CUSTOM_FRAGMENTS, true);
        boolean permissions = intent.getBooleanExtra(EXTRA_PERMISSIONS, true);
        boolean showBack = intent.getBooleanExtra(EXTRA_SHOW_BACK, true);
        boolean showNext = intent.getBooleanExtra(EXTRA_SHOW_NEXT, true);
        boolean skipEnabled = intent.getBooleanExtra(EXTRA_SKIP_ENABLED, true);
        boolean finishEnabled = intent.getBooleanExtra(EXTRA_FINISH_ENABLED, true);
        boolean getStartedEnabled = intent.getBooleanExtra(EXTRA_GET_STARTED_ENABLED, true);

        setFullscreen(fullscreen);

        super.onCreate(savedInstanceState);

        setButtonBackFunction(skipEnabled ? BUTTON_BACK_FUNCTION_SKIP : BUTTON_BACK_FUNCTION_BACK);
        setButtonNextFunction(finishEnabled ? BUTTON_NEXT_FUNCTION_NEXT_FINISH : BUTTON_NEXT_FUNCTION_NEXT);
        setButtonBackVisible(showBack);
        setButtonNextVisible(showNext);
        setButtonCtaVisible(getStartedEnabled);
        setButtonCtaTintMode(BUTTON_CTA_TINT_MODE_TEXT);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.title_material_metaphor)
                .description(R.string.description_material_metaphor)
                .image(R.drawable.art_material_metaphor)
                .background(R.color.color_material_metaphor)
                .backgroundDark(R.color.color_dark_material_metaphor)
                .scrollable(scrollable)
                .buttonCtaLabel("Next")
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast toast = Toast.makeText(MainIntroActivity.this, R.string.toast_button_cta, Toast.LENGTH_SHORT);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        //toast.show();

                        nextSlide();
                    }
                })
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.title_material_bold)
                .description(R.string.description_material_bold)
                .image(R.drawable.art_material_bold)
                .background(R.color.color_material_bold)
                .backgroundDark(R.color.color_dark_material_bold)
                .scrollable(scrollable)
                .buttonCtaLabel("Next")
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast toast = Toast.makeText(MainIntroActivity.this, R.string.toast_button_cta, Toast.LENGTH_SHORT);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        //toast.show();

                        nextSlide();
                    }
                })
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.title_material_motion)
                .description(R.string.description_material_motion)
                .image(R.drawable.art_material_motion)
                .background(R.color.color_material_motion)
                .backgroundDark(R.color.color_dark_material_motion)
                .scrollable(scrollable)
                .buttonCtaLabel("Next")
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast toast = Toast.makeText(MainIntroActivity.this, R.string.toast_button_cta, Toast.LENGTH_SHORT);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        //toast.show();

                        nextSlide();
                    }
                })
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.title_material_shadow)
                .description(R.string.description_material_shadow)
                .image(R.drawable.art_material_shadow)
                .background(R.color.color_material_shadow)
                .backgroundDark(R.color.color_dark_material_shadow)
                .scrollable(scrollable)
                .buttonCtaLabel("Next")
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast toast = Toast.makeText(MainIntroActivity.this, R.string.toast_button_cta, Toast.LENGTH_SHORT);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        //toast.show();

                        nextSlide();
                    }
                })
                .build());

        final Slide permissionsSlide;
        if (permissions) {
            permissionsSlide = new SimpleSlide.Builder()
                    .title(R.string.title_permissions)
                    .description(R.string.description_permissions)
                    .background(R.color.color_permissions)
                    .backgroundDark(R.color.color_dark_permissions)
                    .scrollable(scrollable)
                    .permissions(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE})
                    .build();
            addSlide(permissionsSlide);
        } else {
            permissionsSlide = null;
        }

        final Slide loginSlide;
        if (customFragments) {
            loginSlide = new FragmentSlide.Builder()
                    .background(R.color.color_custom_fragment_1)
                    .backgroundDark(R.color.color_dark_custom_fragment_1)
                    .fragment(LoginFragment.newInstance())
                    .build();
            addSlide(loginSlide);

            /*addSlide(new FragmentSlide.Builder()
                    .background(R.color.color_custom_fragment_2)
                    .backgroundDark(R.color.color_dark_custom_fragment_2)
                    .fragment(R.layout.fragment_custom, R.style.AppThemeDark)
                    .buttonCtaLabel("start")
                    .buttonCtaClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent1 = new Intent(getApplicationContext(),AccountActivity.class);
                            startActivity(intent1);
                        }
                    })
                    .canGoForward(true)

                    .build());*/


        } else {
            loginSlide = null;
        }

        //Feel free to add a navigation policy to define when users can go forward/backward

        setNavigationPolicy(new NavigationPolicy() {
            @Override
            public boolean canGoForward(int position) {
                return true;
            }

            @Override
            public boolean canGoBackward(int position) {
                return true;
            }
        });


        addOnNavigationBlockedListener(new OnNavigationBlockedListener() {
            @Override
            public void onNavigationBlocked(int position, int direction) {
                View contentView = findViewById(android.R.id.content);
                if (contentView != null) {
                    Slide slide = getSlide(position);

                    if (slide == permissionsSlide) {
                        Snackbar.make(contentView, R.string.label_grant_permissions, Snackbar.LENGTH_LONG).show();
                    } else if ((slide == loginSlide)&&(loginSlide.canGoForward())) {

                        mAuth = FirebaseAuth.getInstance();


                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final String string = dataSnapshot.child("level").getValue().toString().trim();

                                switch (string) {
                                    case "4": {
                                        Intent intent = new Intent(getApplicationContext(), AccountAdminPanel.class);
                                        startActivity(intent);
                                        finish();
                                        break;
                                    }
                                    case "99": {
                                        Intent intent = new Intent(getApplicationContext(), AwatingForApproval.class);
                                        startActivity(intent);
                                        finish();
                                        break;
                                    }
                                    case "2": {
                                        Intent intent1 = new Intent(getApplicationContext(), AccountActivityAdmin.class);
                                        startActivity(intent1);
                                        finish();
                                        break;
                                    }
                                    case "3": {
                                        Intent intent1 = new Intent(getApplicationContext(), AccountActivityAuthority.class);
                                        startActivity(intent1);
                                        finish();
                                        break;
                                    }
                                    default: {
                                        Intent intent1 = new Intent(getApplicationContext(), AccountActivityUser.class);
                                        startActivity(intent1);
                                        finish();
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                        Snackbar.make(contentView, R.string.label_fill_out_form, Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        //Feel free to add and remove page change listeners
        /*
        addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        */
    }
}
