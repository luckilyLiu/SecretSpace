package com.hello.mihe.app.launcher.itemDecoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.State;

import com.hello.sandbox.common.util.MetricsUtil;

public class GridSpacesDecoration extends RecyclerView.ItemDecoration {

    private final static int span = 4;

    @Override
    public void getItemOffsets(
            @NonNull Rect outRect,
            @NonNull View view,
            @NonNull RecyclerView parent,
            @NonNull State state) {
        int pos = parent.getChildAdapterPosition(view);
        if (pos == RecyclerView.NO_POSITION) {
            return;
        }
        Adapter adapter = parent.getAdapter();
        if (adapter == null) {
            return;
        }

        int column = (pos % span);
        if (column == 0) { // 第一列
            outRect.left = 0;
            outRect.right = MetricsUtil.DP_24;
        } else if (column == 1 || column==2) { // 第二列
            outRect.left = MetricsUtil.DP_12;
            outRect.right = MetricsUtil.DP_12;
        } else { // 第三列
            outRect.left = MetricsUtil.DP_24;
            outRect.right = 0;
        }

    }
}
