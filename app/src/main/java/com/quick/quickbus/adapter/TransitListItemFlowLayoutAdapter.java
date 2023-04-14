package com.quick.quickbus.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quick.quickbus.R;
import com.quick.quickbus.ui.FlowLayout;
import com.quick.quickbus.ui.TagAdapter;
import com.quick.quickbus.ui.TagInfo;

import java.util.List;

/**
 * 流式布局适配器
 */
public class TransitListItemFlowLayoutAdapter extends TagAdapter<TagInfo> {

    private final Context mContext;

    private final ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);

    public TransitListItemFlowLayoutAdapter(Context ctx, List<TagInfo> datas) {
        super(datas);

        lp.leftMargin = 0;
        lp.rightMargin = dip2px(3);
        lp.topMargin = dip2px(5);
        lp.bottomMargin = dip2px(5);

        mContext = ctx;
    }

    @Override
    public View getView(FlowLayout parent, int position, TagInfo tagInfo) {

        View view = View.inflate(this.mContext, R.layout.bus_flowlayout_item, null);
        ImageView arrow = view.findViewById(R.id.iv_transfer_icon);
        TextView stepDesc = view.findViewById(R.id.tv_step_desc);

        // 第一个item不展示箭头
        if (position == 0) {
            arrow.setVisibility(View.GONE);
        } else {
            arrow.setVisibility(View.VISIBLE);
        }
        view.setLayoutParams(lp);

        stepDesc.setText(tagInfo.getTagDesc());
        stepDesc.setPadding(dip2px(6), dip2px(1), dip2px(6), dip2px(1));

        return view;
    }

    private int dip2px(int dip) {
        if (mContext == null) {
            return 0;
        }
        return (int) (0.5F + mContext.getResources().getDisplayMetrics().density * dip);
    }

}
