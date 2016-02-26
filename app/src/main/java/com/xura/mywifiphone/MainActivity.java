package com.xura.mywifiphone;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.xura.mywifiphone.Utils.FabAnimation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    public static final boolean DEBUG = false;

    private Toolbar appToolbar;
    private DrawerLayout mDrawerLayout;
    private TabLayout tabLayout;
    private int lastSelectedTab = 0;

    private static final int REQUEST_CONTACTS = 0;
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS};
    private View mLayout;

    private int[] tabIconsSelected = {
        R.drawable.ic_star_white_24dp,
        R.drawable.ic_access_time_white_24dp,
        R.drawable.ic_contact_phone_white_24dp
    };
    private int[] tabIconsUnselected = {
            R.drawable.ic_star_grey_24dp,
            R.drawable.ic_access_time_grey_24dp,
            R.drawable.ic_contact_phone_grey_24dp
    };

    private FloatingActionButton mFab;
    private FabAnimation mFabAnim;
    /**
     * Fragment containing the dialpad that slides into view
     */


    private void checkPermissionsBeforeSetup() {

        String contactPerm = Manifest.permission.READ_CONTACTS;
        int granted = PackageManager.PERMISSION_GRANTED;

        if (ContextCompat.checkSelfPermission(MainActivity.this, contactPerm) != granted) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, contactPerm)) {

                //Explain to the user why we need to read the contacts
                Snackbar.make(mLayout, R.string.permission_contacts_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        PERMISSIONS_CONTACT, REQUEST_CONTACTS);
                            }
                        })
                        .show();

            } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            PERMISSIONS_CONTACT, REQUEST_CONTACTS);
            }
        } else {

            setupActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setupActivity();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissionsBeforeSetup();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mFab == null) {
            mFab = (FloatingActionButton) findViewById(R.id.fab);
        }
        mFab.show();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private void setupActivity() {
        appToolbar = (Toolbar) findViewById(R.id.top_app_bar);
        setSupportActionBar(appToolbar);

        /*
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            View dropshadow = (View) findViewById(R.id.toolbar_dropshadow_view);
            dropshadow.setVisibility(View.GONE);
        }
        */

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);

        }
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFabAnim = new FabAnimation(this, viewPager, mFab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mFabAnim.hide("launchDialer",80);
                }
            });

        tabLayout = (TabLayout) findViewById(R.id.tabs_app_bar);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons(tabLayout);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                viewPager.setCurrentItem(pos);
                tab.setIcon(tabIconsSelected[pos]);
                if ((pos == 0) || (lastSelectedTab == 0)) {
                    if (pos == 0) {
                        mFabAnim.moveRight();
                    } else {
                        mFabAnim.moveLeft();
                    }
                }
                lastSelectedTab = pos;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                tab.setIcon(tabIconsUnselected[pos]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                tab.setIcon(tabIconsSelected[pos]);
            }
        });
    }

    public void launchDialer() {
        Intent intent = new Intent(MainActivity.this, DialerActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this);
        ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
            case R.id.navigate:
                startActivity(new Intent(this, AccountSettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new FavoritesFragment(), "Favorites");
        adapter.addFragment(new HistoryFragment(), "History");
        adapter.addFragment(new ContactFragment(), "Contacts");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons(TabLayout tabLayout) {
        tabLayout.getTabAt(0).setIcon(tabIconsSelected[0]);
        tabLayout.getTabAt(1).setIcon(tabIconsUnselected[1]);
        tabLayout.getTabAt(2).setIcon(tabIconsUnselected[2]);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);

        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Remove Title from the tab bar:
            //return mFragmentTitles.get(position);
            return null;
        }
    }
}
