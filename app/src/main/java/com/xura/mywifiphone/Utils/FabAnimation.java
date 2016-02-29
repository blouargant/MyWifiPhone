/*
 * Created by bertrand Louargant on 26/02/16
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

package com.xura.mywifiphone.Utils;

import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.xura.mywifiphone.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class FabAnimation {
    private FloatingActionButton mFab;
    private AppCompatActivity mActivity;
    private ViewPager mView;
    private final int RIGHT = 0;
    private final int LEFT = 1;
    private final int BEFORE = 0;
    private final int AFTER = 1;

    public FabAnimation(AppCompatActivity activity, ViewPager view, FloatingActionButton fab) {
        mActivity = activity;
        mFab = fab;
        mView = view;
    }

    public void hide(String function){
        mFab.hide();
        try {
            Method method = mActivity.getClass().getMethod(function);
            final Object invoke = method.invoke(mActivity);
        } catch (Exception e) {
            System.out.print(e);
        }
    }
    public void hide(String function, int sleep){
        WaitFabAnimation task = new WaitFabAnimation(mFab, mView, function, AFTER, "hide");
        task.execute(sleep);
    }

    public void show(String function){
        mFab.hide();
        try {
            Method method = mActivity.getClass().getMethod(function);
            final Object invoke = method.invoke(mActivity);
        } catch (Exception e) {
            System.out.print(e);
        }
    }
    public void show(String function, int sleep){
        if (mView.getWidth() == 0) { //Screen is rotated, wait for views to be initialized
            sleep += 100;
        }
        WaitFabAnimation task = new WaitFabAnimation(mFab, mView, function, BEFORE, "show");
        task.execute(sleep);
    }

    public void moveRight(){
        moveFloatingActionButton(RIGHT);
    }
    public void moveLeft(){
        moveFloatingActionButton(LEFT);
    }

    private void moveFloatingActionButton(int position) {
        int fab_margin = (int) mActivity.getResources().getDimension(R.dimen.fab_margin);
        int timewait = 1;

        if (mView.getWidth() == 0) { //Screen is rotated, wait for views to be initialized
            timewait = 200;
        }
        LeftRightFabAnimation task = new LeftRightFabAnimation(mFab, mView, fab_margin, position);
        task.execute(timewait);
    }


    class WaitFabAnimation extends AsyncTask<Integer, Void, Void> {
        private final WeakReference<FloatingActionButton> FabReference;
        private final WeakReference<ViewPager> viewReference;
        private final String animation;
        private final int position;
        private final String function;

        public WaitFabAnimation(FloatingActionButton mFab,
                                ViewPager viewPager,
                                String func,
                                int exec_pos,
                                String anim) {
            FabReference = new WeakReference<FloatingActionButton>(mFab);
            viewReference = new WeakReference<ViewPager>(viewPager);
            animation = anim;
            position = exec_pos;
            function = func;
        }
        @Override
        protected void onPreExecute() {
            if (position == BEFORE) {
                try {
                    Method method = mActivity.getClass().getMethod(function);
                    final Object invoke = method.invoke(mActivity);
                } catch (Exception e) {
                    System.out.print(e);
                }
            } else {
                final FloatingActionButton mFab = FabReference.get();
                switch (animation) {
                    case "hide":
                        mFab.hide();
                        break;
                    case "show":
                        mFab.show();
                        break;
                    default:
                        break;
                }
            }
            super.onPreExecute();
        }
        // Do in background.
        @Override
        protected Void doInBackground(Integer... params) {
            try {
                TimeUnit.MILLISECONDS.sleep(params[0]);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void res) {
            // this is to be sure that the view is still active
            final FloatingActionButton mFab = FabReference.get();
            final ViewPager viewPager = viewReference.get();
            if ((mFab != null) && (viewPager != null)) {
                if (position == AFTER) {
                    try {
                        Method method = mActivity.getClass().getMethod(function);
                        final Object invoke = method.invoke(mActivity);
                    } catch (Exception e) {
                        System.out.print(e);
                    }
                } else {
                    switch (animation) {
                        case "hide":
                            mFab.hide();
                            break;
                        case "show":
                            mFab.show();
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    class LeftRightFabAnimation extends AsyncTask<Integer, Void, Void> {
        private final WeakReference<FloatingActionButton> FabReference;
        private final WeakReference<ViewPager> viewReference;
        private final int fab_margin;
        private final int pos;

        public LeftRightFabAnimation(FloatingActionButton mFab, ViewPager viewPager,
                                int margin, int position) {
            FabReference = new WeakReference<FloatingActionButton>(mFab);
            viewReference = new WeakReference<ViewPager>(viewPager);
            fab_margin = margin;
            pos = position;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        // Do in background.
        @Override
        protected Void doInBackground(Integer... params) {
            try {
                TimeUnit.MILLISECONDS.sleep(params[0]);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void res) {
            if (FabReference != null) {
                final FloatingActionButton mFab = FabReference.get();
                final ViewPager viewPager = viewReference.get();
                if ((mFab != null) && (viewPager != null)) {
                    int fabpos = (viewPager.getWidth() / 2) - (mFab.getWidth() / 2) - fab_margin;
                    int final_pos = fabpos;
                    int start_pos = 0;
                    long duration = Math.abs(Math.round(fabpos * 1.6));

                    if (pos == 0) {
                        start_pos = fabpos;
                        final_pos = 0;
                    }
                    //TranslateAnimation moveFab;
                    ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(mFab, "translationX", start_pos, final_pos);
                    objectAnimator.setDuration(duration);
                    objectAnimator.start();
                }
            }
        }
    }
}
