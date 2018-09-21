package com.xujiaji.library;

import android.content.Context;

/**
 * author: xujiaji
 * created on: 2018/9/20 11:06
 * description: 工具类
 */
public class RippleCheckBoxUtil {
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
