package com.xura.mywifiphone;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.android.phone.common.animation.AnimUtils;
import com.android.phone.common.animation.AnimationListenerAdapter;
import com.xura.mywifiphone.MainActivity;
import com.xura.mywifiphone.Dialer.DialpadFragment;
import com.xura.mywifiphone.Utils.DialerUtils;

import junit.framework.Assert;

public class DialerActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    protected DialpadFragment mDialpadFragment;
    public static final boolean DEBUG = MainActivity.DEBUG;
    private ViewPager mViewPager;
    private FloatingActionButton mFabDial;
    /**
     * Animation that slides in.
     */
    private Animation mSlideIn;
    AnimationListenerAdapter mSlideInListener = new AnimationListenerAdapter() {
        @Override
        public void onAnimationEnd(Animation animation) {
            //maybeEnterSearchUi();
        }
    };
    /**
     * Animation that slides out.
     */
    private Animation mSlideOut;
    /**
     * Listener for after slide out animation completes on dialer fragment.
     */
    AnimationListenerAdapter mSlideOutListener = new AnimationListenerAdapter() {
        @Override
        public void onAnimationEnd(Animation animation) {
            commitDialpadFragmentHide();
        }
    };
    /**
     * Whether or not the device is in landscape orientation.
     */
    private boolean mIsLandscape;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialer_layout);

        Toolbar appToolbar = (Toolbar) findViewById(R.id.top_app_bar);
        setSupportActionBar(appToolbar);
        // Remove the tab view
        TabLayout Tab_layout = (TabLayout) findViewById(R.id.tabs_app_bar);
        Tab_layout.setVisibility(View.GONE);

        /*
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        */
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.settings_drawer_layout);

        mFabDial = (FloatingActionButton) findViewById(R.id.fab_dial);
        mFabDial.setTranslationY(mFabDial.getHeight() + 16);

        //mFab.setVisibility(View.GONE);

        mViewPager = (ViewPager) findViewById(R.id.dialer_viewpager);
        /*
        if (viewPager != null) {
            setupViewPager(viewPager);

        }*/
        mIsLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;


        final boolean isLayoutRtl = DialerUtils.isRtl();
        if (mIsLandscape) {
            mSlideIn = AnimationUtils.loadAnimation(this,
                    isLayoutRtl ? R.anim.dialpad_slide_in_left : R.anim.dialpad_slide_in_right);
            mSlideOut = AnimationUtils.loadAnimation(this,
                    isLayoutRtl ? R.anim.dialpad_slide_out_left : R.anim.dialpad_slide_out_right);
        } else {
            mSlideIn = AnimationUtils.loadAnimation(this, R.anim.dialpad_slide_in_bottom);
            mSlideOut = AnimationUtils.loadAnimation(this, R.anim.dialpad_slide_out_bottom);
        }

        mSlideIn.setInterpolator(AnimUtils.EASE_IN);
        mSlideOut.setInterpolator(AnimUtils.EASE_OUT);

        mSlideIn.setAnimationListener(mSlideInListener);
        mSlideOut.setAnimationListener(mSlideOutListener);


        showDialpadFragment(true);

    }
    /*
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

// TODO
    /**
     * Initiates a fragment transaction to show the dialpad fragment. Animations and other visual
     * updates are handled by a callback which is invoked after the dialpad fragment is shown.
     * @see #onDialpadShown
     */
    private void showDialpadFragment(boolean animate) {

        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (mDialpadFragment == null) {
            mDialpadFragment = new DialpadFragment();
            ft.add(R.id.dialerCoordinatorLayout, mDialpadFragment, "dialpad");
        } else {
            ft.show(mDialpadFragment);
        }

        mDialpadFragment.setAnimate(animate);
        ft.commit();
        //mActionBarController.onDialpadUp();

        mViewPager.animate().alpha(0).withLayer();
    }

    /**
     * Callback from child DialpadFragment when the dialpad is shown.
     */
    public void onDialpadShown() {

        Assert.assertNotNull(mDialpadFragment);
        if (mDialpadFragment.getAnimate()) {
            if (mDialpadFragment.getView() != null) {
                Log.d("DialerActivity", "mDialpadFragment.getView():" + mDialpadFragment.getView());
                mDialpadFragment.getView().startAnimation(mSlideIn);
            } else {
                Log.d("DialerActivity", "mDialpadFragment.getView() is null");
            }
        } else {
            mDialpadFragment.setYFraction(0);
        }

        //mFab.show();
        //updateSearchFragmentPosition();

        mFabDial.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

    }

    /**
     * Initiates animations and other visual updates to hide the dialpad. The fragment is hidden in
     * a callback after the hide animation ends.
     * @see #commitDialpadFragmentHide
     */
    public void hideDialpadFragment(boolean animate, boolean clearDialpad) {
        /*
        if (mDialpadFragment == null || mDialpadFragment.getView() == null) {
            return;
        }
        if (clearDialpad) {
            mDialpadFragment.clearDialpad();
        }
        if (!mIsDialpadShown) {
            return;
        }
        mIsDialpadShown = false;
        mDialpadFragment.setAnimate(animate);
        mListsFragment.setUserVisibleHint(true);
        mListsFragment.sendScreenViewForCurrentPosition();

        updateSearchFragmentPosition();

        mFloatingActionButtonController.align(getFabAlignment(), animate);
        if (animate) {
            mDialpadFragment.getView().startAnimation(mSlideOut);
        } else {
            commitDialpadFragmentHide();
        }

        mActionBarController.onDialpadDown();

        if (isInSearchUi()) {
            if (TextUtils.isEmpty(mSearchQuery)) {
                exitSearchUi();
            }
        }
        */
    }

    /**
     * Finishes hiding the dialpad fragment after any animations are completed.
     */
    private void commitDialpadFragmentHide() {
        /*
        if (!mStateSaved && mDialpadFragment != null && !mDialpadFragment.isHidden()) {
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.hide(mDialpadFragment);
            ft.commit();
        }
        mFloatingActionButtonController.scaleIn(AnimUtils.NO_DELAY);
        */
    }


}
