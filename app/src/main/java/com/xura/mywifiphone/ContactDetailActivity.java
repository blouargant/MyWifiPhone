/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xura.mywifiphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ContactDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "contact_name";
    public static final String CONTACT_KEY = "parceable";
    public static final String BACKGROUND = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        Intent intent = getIntent();
        //final String contactName = intent.getStringExtra(EXTRA_NAME);
        final ContactInfo mContact = (ContactInfo) intent.getParcelableExtra(CONTACT_KEY);
        final String contactName = mContact.getName();
        final int defaultBackground = mContact.getDefaultBackground();

        Toolbar appToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(appToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(contactName);

        loadBackdrop(defaultBackground);
    }

    private void loadBackdrop(int backdrop) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        //Glide.with(this).load(Contacts.getDrawable()).centerCrop().into(imageView);
        Glide.with(this).load(backdrop).fitCenter().into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_actions, menu);
        return true;
    }
}

