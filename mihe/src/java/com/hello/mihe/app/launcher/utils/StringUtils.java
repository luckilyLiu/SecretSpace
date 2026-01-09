package com.hello.mihe.app.launcher.utils;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.hello.mihe.app.launcher.ui.act.web.WebviewAct;

import java.util.regex.Pattern;

public class StringUtils {

    public static String[] ALL_LETTERS = {
            "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z"
    };


    public static SpannableStringBuilder getLinkSubstringWithColorToWebView(
            Activity act, int hightLightColor, String o, String... pairs) {
        SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(o);
        for (int i = 0; i < pairs.length; i += 2) {
            String text = pairs[i], link = pairs[i + 1];
            clickableHtmlBuilder.setSpan(
                    new ClickableSpan() {
                        public void onClick(View view) {
                            String curTitle = text.replace("《", "").replace("》", "");
                            WebviewAct.Companion.start(act, link, curTitle, false);
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            ds.setColor(hightLightColor);
                        }
                    },
                    o.indexOf(text),
                    o.indexOf(text) + text.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return clickableHtmlBuilder;
    }

    public static boolean isLetter(String str) {
        char c = str.charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        return pattern.matcher(c + "").matches();
    }
}
