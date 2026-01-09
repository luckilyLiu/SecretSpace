package com.hello.mihe.app.launcher.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;

import com.android.launcher3.BuildConfig;
import com.android.launcher3.R;
import com.hello.sandbox.common.util.MarketUtil;
import com.hello.sandbox.common.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/** Created by san on 10/01/2018. */
public final class MarketHelper extends MarketUtil {

    private static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";
    public static boolean hasLauncherGooglePlayApp = false;

    public static boolean shouldOpenOtherAppMarket() {
        // only none-google channel with none-googlePlay installed is true
        return MarketUtil.shouldOpenOtherAppMarket();
    }

    private static Intent getGoToAppMargetIntent(Context context, String packageName) {
        return getGoToAppMargetIntent(context, packageName, false);
    }

    public static Intent getGoToAppMargetIntent(
            Context context, String packageName, boolean callFromProfileOwner) {
        // none-google channel should not open Google Play.
        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
        marketIntent.setData(Uri.parse("market://details?id=" + packageName));

        final List<ResolveInfo> resInfo =
                context
                        .getPackageManager()
                        .queryIntentActivities(
                                marketIntent,
                                callFromProfileOwner ? PackageManager.GET_UNINSTALLED_PACKAGES : 0);
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        List<Intent> systemShareIntents = new ArrayList<Intent>();
        for (ResolveInfo info : resInfo) {

            Intent targeted = new Intent(Intent.ACTION_VIEW);
            targeted.setData(Uri.parse("market://details?id=" + packageName));
            ActivityInfo activityInfo = info.activityInfo;
            targeted.setPackage(activityInfo.packageName);
            targeted.setClassName(activityInfo.packageName, activityInfo.name);
            targetedShareIntents.add(targeted);
            if ((info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                if (GOOGLE_PLAY_PACKAGE_NAME.equals(
                        info.activityInfo.applicationInfo.packageName)) {
                    systemShareIntents.add(0, targeted);
                } else {
                    systemShareIntents.add(targeted);
                }
            }
        }
        if (systemShareIntents.size() != 0) {
            return systemShareIntents.get(0);
        }
        if (targetedShareIntents.isEmpty()) {
            return null;
        }
        if (targetedShareIntents.size() > 1) {
            // 如果有多个商店，让用户选择
            Intent chooserIntent =
                    Intent.createChooser(
                            targetedShareIntents.remove(0),
                            context.getString(R.string.mihe_update_go_to_market));
            if (chooserIntent == null) {
                return null;
            }
            chooserIntent.putExtra(
                    Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[0]));
            return chooserIntent;
        } else {
            // 如果只有一个符合条件的商店，直接跳转
            return targetedShareIntents.get(0);
        }
    }

    public static void goToAppMarket(Activity act, String url) {
        String packageName = getPackageNameFromUrl(url);
        Intent intent = getGoToAppMargetIntent(act, packageName);
        if (intent != null) {
            act.startActivity(intent);
        } else {
            intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            if (intent.resolveActivity(act.getPackageManager()) != null) {
                act.startActivity(Intent.createChooser(intent, "请选择浏览器"));
            }
        }
    }

    public static void goToAppMarketWithPackageName(Activity act, String packageName) {
        Intent intent = getGoToAppMargetIntent(act, packageName);
        if (intent != null) {
            try {
                act.startActivity(intent);
            } catch (Exception ignored) {
            }
        }
    }

    public static void profileOwnerGoToAppMarketWithPackageName(Activity act, String packageName) {
        Intent intent = getGoToAppMargetIntent(act, packageName, true);
        if (intent != null) {
            try {
                act.startActivity(intent);
            } catch (Exception ignored) {
            }
        }
    }

    public static void gotoMarket(Context context, Intent intent) {
        hasLauncherGooglePlayApp = true;
        if (intent != null) {
            try {
                context.startActivity(intent);
            } catch (Exception ignored) {
                if (BuildConfig.DEBUG) {
                    ToastUtil.message("打开应用商店出错");
                }
            }
        } else {
            ToastUtil.message("没有找到应用商店");
        }
    }

    private static String getPackageNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
