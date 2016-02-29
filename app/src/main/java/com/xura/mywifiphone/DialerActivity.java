package com.xura.mywifiphone;

import android.animation.ObjectAnimator;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.text.TextUtils;
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
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.android.phone.common.animation.AnimUtils;
import com.android.phone.common.animation.AnimationListenerAdapter;
import com.xura.mywifiphone.MainActivity;
import com.xura.mywifiphone.Dialer.DialpadFragment;
import com.xura.mywifiphone.Utils.DialerUtils;
import com.xura.mywifiphone.Utils.FabAnimation;

import junit.framework.Assert;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

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
    private FabAnimation mFabAnim;
    private boolean mStateSaved;
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
        //ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.settings_drawer_layout);
        mViewPager = (ViewPager) findViewById(R.id.dialer_viewpager);

        mFabDial = (FloatingActionButton) findViewById(R.id.fab_dial);
        mFabAnim = new FabAnimation(this, mViewPager, mFabDial);
        mFabDial.setVisibility(View.GONE);

        /*
        if (viewPager != null) {
            setupViewPager(viewPager);

        }*/
        mIsLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;


        final boolean isLayoutRtl = DialerUtils.isRtl(mViewPager);
        if (mIsLandscape) {
            mSlideIn = AnimationUtils.loadAnimation(this,
                    isLayoutRtl ? R.anim.dialpad_slide_in_left : R.anim.dialpad_slide_in_right);
            mSlideOut = AnimationUtils.loadAnimation(this,
                    isLayoutRtl ? R.anim.dialpad_slide_out_left : R.anim.dialpad_slide_out_right);
        } else {
            mSlideIn = AnimationUtils.loadAnimation(this, R.anim.dialpad_slide_in_bottom);
            mSlideOut = AnimationUtils.loadAnimation(this, R.anim.dialpad_slide_out_bottom);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSlideIn.setInterpolator(AnimUtils.EASE_IN);
            mSlideOut.setInterpolator(AnimUtils.EASE_OUT);
        }

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
            hideDialpadFragment(true,false);
            return true;
            //NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mFabDial == null) {
            mFabDial = (FloatingActionButton) findViewById(R.id.fab_dial);
        }
        mFabDial.setVisibility(View.GONE);
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mStateSaved = false;
    }
    @Override
    public void onBackPressed() {
        if (mStateSaved) {
            return;
        }
        hideDialpadFragment(true, false);

        /*
        if (mIsDialpadShown) {
            if (TextUtils.isEmpty(mSearchQuery) ||
                    (mSmartDialSearchFragment != null && mSmartDialSearchFragment.isVisible()
                            && mSmartDialSearchFragment.getAdapter().getCount() == 0)) {
                exitSearchUi();
            }
            hideDialpadFragment(true, false);
        } else if (isInSearchUi()) {
            exitSearchUi();
            DialerUtils.hideInputMethod(mParentLayout);

        }*/

        //super.onBackPressed();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mStateSaved = true;
    }

// TODO
    /**
     * Initiates a fragment transaction to show the dialpad fragment. Animations and other visual
     * updates are handled by a callback which is invoked after the dialpad fragment is shown.
     * @see #onDialpadShown
     */
    private void showDialpadFragment(boolean animate) {
        if (mStateSaved) {
            return;
        }

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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            mViewPager.animate().alpha(0);
        } else {
            mViewPager.animate().alpha(0).withLayer();
        }
    }

    /**
     * Callback from child DialpadFragment when the dialpad is shown.
     */
    public void onDialpadShown() {
        mFabAnim.show("slideFragmentIn", 200);
    }
    public void slideFragmentIn() {
        Assert.assertNotNull(mDialpadFragment);
        if (mDialpadFragment.getAnimate()) {
            if (mDialpadFragment.getView() != null) {
                mDialpadFragment.getView().startAnimation(mSlideIn);
            } else {
                Log.d("DialerActivity", "mDialpadFragment.getView() is null");
            }
        } else {
            mDialpadFragment.setYFraction(0);
        }
    }

    /**
     * Initiates animations and other visual updates to hide the dialpad. The fragment is hidden in
     * a callback after the hide animation ends.
     * @see #commitDialpadFragmentHide
     */
    public void hideDialpadFragment(boolean animate, boolean clearDialpad) {
        Log.d("DialerActivity", "hideDialpadFragment");
        if (mDialpadFragment == null || mDialpadFragment.getView() == null) {
            return;
        }
        if (clearDialpad) {
            mDialpadFragment.clearDialpad();
        }
        mDialpadFragment.setAnimate(animate);

        //updateSearchFragmentPosition();

        //mFloatingActionButtonController.align(getFabAlignment(), animate);
        if (animate) {
            mDialpadFragment.getView().startAnimation(mSlideOut);
        } else {
            commitDialpadFragmentHide();
        }

        //mActionBarController.onDialpadDown();
        /*
        if (isInSearchUi()) {
            if (TextUtils.isEmpty(mSearchQuery)) {
                exitSearchUi();
            }
        }*/
        mFabAnim.hide("backPress", 400);

    }
    public void backPress() {
        super.onBackPressed();
    }

    /**
     * Finishes hiding the dialpad fragment after any animations are completed.
     */
    private void commitDialpadFragmentHide() {
        Log.d("DialerActivity", "commitDialpadFragmentHide");
        if (!mStateSaved && mDialpadFragment != null && !mDialpadFragment.isHidden()) {
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.hide(mDialpadFragment);
            ft.commit();
        }
        //mFloatingActionButtonController.scaleIn(AnimUtils.NO_DELAY);

    }

}
