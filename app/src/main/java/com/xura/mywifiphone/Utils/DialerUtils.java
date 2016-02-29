package com.xura.mywifiphone.Utils;

import android.os.Build;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.view.View;

import java.util.Locale;

/**
 * Created by bertrand on 25/02/16.
 */
public class DialerUtils {

    /**
     * @return True if the application is currently in RTL mode.
     */
    public static boolean isRtl(View view) {
        Boolean rtl = true;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            rtl = ViewUtils.isLayoutRtl(view);

        } else {
            rtl = (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) ==
                    View.LAYOUT_DIRECTION_RTL);
        }
        return rtl;
    }

}
