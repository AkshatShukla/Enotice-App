package com.rcoem.enotice.enotice_app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickListener;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.marcoscg.easylicensesdialog.EasyLicensesDialogCompat;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.rcoem.enotice.enotice_app.AdminClasses.BlockUserPlanel;
import com.webianks.easy_feedback.EasyFeedback;

public class AboutUs extends MaterialAboutActivity {

    @Override
    protected MaterialAboutList getMaterialAboutList(final Context context) {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();

        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text("E-Notice App")
                .icon(R.mipmap.ic_launcher)
                .setOnClickListener(new MaterialAboutItemOnClickListener() {
                    @Override
                    public void onClick(boolean longClick) {
                        Snackbar.make(((AboutUs) context).findViewById(R.id.mal_material_about_activity_coordinator_layout), "Coded with love by CSE, RCOEM", Snackbar.LENGTH_LONG).show();
                    }
                })
                .build());

        try {

            appCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(this,
                    new IconicsDrawable(this)
                            .icon(GoogleMaterial.Icon.gmd_info_outline)
                            .color(ContextCompat.getColor(this, R.color.colorBg))
                            .sizeDp(18),
                    "Version",
                    false));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Changelog")
                .icon(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_history)
                        .color(ContextCompat.getColor(this, R.color.colorBg))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createWebViewDialogOnClickAction(this, "Releases", "https://github.com/daniel-stoneuk/material-about-library/releases", true, false))
                .build());

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Licenses")
                .icon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_book)
                        .color(ContextCompat.getColor(this, R.color.colorBg))
                        .sizeDp(18))
                .setOnClickListener(new MaterialAboutItemOnClickListener() {
                    @Override
                    public void onClick(boolean longClick) {
                        //startActivity(new Intent(getApplicationContext(),Licenses.class));
                        new EasyLicensesDialogCompat(context)
                                .setTitle("Licenses")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                })
                .build());


        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title("The E-Notice Team");

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Dhananjay Hedaoo")
                .subText("hdandroid16@gmail.com")
                .icon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(ContextCompat.getColor(this, R.color.colorBg))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createEmailOnClickAction(this,"hdandroid16@gmail.com","Question concerning E-Notice Board"))
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Akshat Shukla")
                .subText("aks.shukla47@gmail.com")
                .icon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(ContextCompat.getColor(this, R.color.colorBg))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createEmailOnClickAction(this,"aks.shukla47@gmail.com","Question concerning E-Notice Board"))
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Tushar Sakharkar")
                .subText("tushar.sakharkar97@gmail.com")
                .icon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(ContextCompat.getColor(this, R.color.colorBg))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createEmailOnClickAction(this,"tushar.sakharkar97@gmail.com","Question concerning E-Notice Board"))
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Zubin Paul")
                .subText("zubinpaul@outlook.com")
                .icon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(ContextCompat.getColor(this, R.color.colorBg))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createEmailOnClickAction(this,"zubinpaul@outlook.com","Question concerning E-Notice Board"))
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Ravi Patel")
                .subText("patelrr@rknec.edu")
                .icon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(ContextCompat.getColor(this, R.color.colorBg))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createEmailOnClickAction(this,"patelrr@rknec.edu","Question concerning E-Notice Board"))
                .build());

        authorCardBuilder.addItem(ConvenienceBuilder.createEmailItem(this,
                new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_email)
                        .color(ContextCompat.getColor(this, R.color.colorBg))
                        .sizeDp(18),
                "Contact the Team",
                true,
                "enotice.rcoem@gmail.com",
                "Question concerning E-Notice Board"));

        MaterialAboutCard.Builder supportCardBuilder = new MaterialAboutCard.Builder();
        supportCardBuilder.title("Support Development");

        supportCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .icon(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_skull)
                        .color(ContextCompat.getColor(this, R.color.colorBg))
                        .sizeDp(18))
                .text("Report Bug")
                .subText("Send buggy instances of this app to us")
                .setOnClickListener(new MaterialAboutItemOnClickListener() {
                    @Override
                    public void onClick(boolean longClick) {
                        new EasyFeedback.Builder(context)
                                .withEmail("enotice.rcoem@gmail.com")
                                .withSystemInfo()
                                .build()
                                .start();
                    }
                })
                .build()
        );

        supportCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Suggest New Features")
                .subText("We try our best to incorporate most of them")
                .icon(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_lightbulb_on)
                        .color(ContextCompat.getColor(this, R.color.colorBg))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createEmailOnClickAction(this,"enotice.rcoem@gmail.com","New Feature Idea"))
                .build());


        MaterialAboutCard.Builder otherCardBuilder = new MaterialAboutCard.Builder();
        otherCardBuilder.title("E-Notice Projectm");

        otherCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .icon(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_comment_alert)
                        .color(ContextCompat.getColor(this, R.color.colorBg))
                        .sizeDp(18))
                .text("Project Details")
                //.subTextHtml("This is <b>HTML</b> formatted <i>text</i> <br /> This is very cool because it allows lines to get very long which can lead to all kinds of possibilities. <br /> And line breaks.")
                .subTextHtml("<b>Rashtriya Uchchatar Shiksha Abhiyan (RUSA)</b>, which is a Centrally Sponsored Scheme (CSS), launched in 2013 and aims at providing strategic funding to eligible state higher educational institutions, by Ministry of HRD, Govt. of India. The project is about development of an E-Notice Board package for educational institutions to have electronic generation of notices and intra-communication in University colleges.<br/> <i>Nagpur University</i> will be the first university to be a part of the implementation of the project. The project consists of an Android App, Web App and a LED Display Module. ")
                .setIconGravity(MaterialAboutActionItem.GRAVITY_TOP)
                .build()
        );

        MaterialAboutCard.Builder poweredByCardBuilder = new MaterialAboutCard.Builder();
        poweredByCardBuilder.title("Powered By");

        poweredByCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text("Firebase")
                .icon(R.drawable.firebase_icon)
                .setOnClickListener(new MaterialAboutItemOnClickListener() {
                    @Override
                    public void onClick(boolean longClick) {
                        Snackbar.make(((AboutUs) context).findViewById(R.id.mal_material_about_activity_coordinator_layout), "Secret behind real-time sync", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .build());

        poweredByCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text("MySQL")
                .icon(R.drawable.mysql_icon)
                .setOnClickListener(new MaterialAboutItemOnClickListener() {
                    @Override
                    public void onClick(boolean longClick) {
                        Snackbar.make(((AboutUs) context).findViewById(R.id.mal_material_about_activity_coordinator_layout), "Secret behind push notifications", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .build());


        return new MaterialAboutList(appCardBuilder.build(), authorCardBuilder.build(), supportCardBuilder.build(), otherCardBuilder.build(), poweredByCardBuilder.build());
    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_about_us);
    }
}
