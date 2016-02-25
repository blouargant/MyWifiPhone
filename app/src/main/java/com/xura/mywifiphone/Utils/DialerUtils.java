package com.xura.mywifiphone.Utils;

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
    public static boolean isRtl() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) ==
                View.LAYOUT_DIRECTION_RTL;
    }

}
