package com.droidlib.kompose.utils

import android.content.res.Resources




fun dpToPx(dp: Float): Float {
    return dp * Resources.getSystem().getDisplayMetrics().density
}

fun pxToDp(px: Float): Float {
    return px / Resources.getSystem().getDisplayMetrics().density
}