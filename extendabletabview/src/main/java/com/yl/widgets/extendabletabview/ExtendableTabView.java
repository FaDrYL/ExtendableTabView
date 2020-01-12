package com.yl.widgets.extendabletabview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.ArrayList;

/**
 * ExtendableTabView
 * Author: _YL_
 *
 */
public class ExtendableTabView extends FrameLayout {
    public static final int ORIENTATION_UP = 0;
    public static final int ORIENTATION_DOWN = 1;
    public static final int ORIENTATION_LEFT= 2;
    public static final int ORIENTATION_RIGHT = 3;

    private String TAG = "ExtendableTabView";
    private Context context;
    private LinearLayout ll_tab_inner;
    private LinearLayout ll_body;
    private View tv_select_anim;

    private boolean isExtended = false;

    OnEventListener onEventListener;

    // style
    private int orientation_selector = ORIENTATION_LEFT;   // open
    private int orientation_extend = ORIENTATION_UP;     // body

    // Dimensions
    private float tab_width;               // size of open & close bottom, unit: dp
    private float tab_height;              // size of open & close bottom, unit: dp
    private float tab_textSize = 16;            // unit: sp

    // Color
    private int tab_textColor_selected = Color.parseColor("#ffD81B60");
    private int tab_textColor_unselected = Color.parseColor("#ff000000");
    private int tab_bgColor_selected = Color.parseColor("#ffcccccc");
    private int tab_bgColor_unselected = Color.parseColor("#ffeeeeee");
    private int tab_imageColor_tint = Color.parseColor("#ff000000");

    private int tab_pos_old = -1;
    private View v_main;

    public ExtendableTabView(Context context){
        super(context);
        this.context = context;
        init(null);
    }

    public ExtendableTabView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs){
        init_attrs(attrs);
        init_view();
    }

    private void init_attrs(AttributeSet attrs){
        if(attrs != null) {
            final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ExtendableTabView, 0, 0);
            try {
                orientation_selector = ta.getInt(R.styleable.ExtendableTabView_orientation_selector, ORIENTATION_LEFT);
                orientation_extend = ta.getInt(R.styleable.ExtendableTabView_orientation_extend, ORIENTATION_UP);

                tab_textSize = ta.getDimension(R.styleable.ExtendableTabView_tab_textSize, 16);
                tab_width = ta.getDimension(R.styleable.ExtendableTabView_tab_width, getResources().getDimensionPixelSize(R.dimen.tab_width));
                tab_height = ta.getDimension(R.styleable.ExtendableTabView_tab_height, getResources().getDimensionPixelSize(R.dimen.tab_height));

                tab_textColor_selected = ta.getColor(R.styleable.ExtendableTabView_tab_textColor_selected, Color.parseColor("#ffD81B60"));
                tab_textColor_unselected = ta.getColor(R.styleable.ExtendableTabView_tab_textColor_unselected, Color.parseColor("#ff000000"));
                tab_bgColor_selected = ta.getColor(R.styleable.ExtendableTabView_tab_background_selected, Color.parseColor("#ffcccccc"));
                tab_bgColor_unselected = ta.getColor(R.styleable.ExtendableTabView_tab_background_unselected, Color.parseColor("#ffeeeeee"));
                tab_imageColor_tint = ta.getColor(R.styleable.ExtendableTabView_tab_imageColor_tint, Color.parseColor("#ff000000"));
            } finally {
                ta.recycle();
            }
        }
    }

    private void init_view(){
        init_tabView();

        final ImageView iv_tab_open = v_main.findViewById(R.id.tab_open);
        final ImageView iv_tab_close = v_main.findViewById(R.id.tab_close);
        final ConstraintLayout ll_tab = v_main.findViewById(R.id.tab_cl);
        ll_tab.setVisibility(View.GONE);
        ll_tab_inner = v_main.findViewById(R.id.tab_inner_ll);
        ll_body = v_main.findViewById(R.id.tab_body_ll);
        ll_body.setWeightSum(1);
        ll_body.setVisibility(View.GONE);
        tv_select_anim = v_main.findViewById(R.id.tab_select_anim);
        int resId_drawableOpen_arrow;
        int resId_drawableClose_arrow;
        int resId_drawable_bg;
        switch (orientation_selector){
            case 0:
                resId_drawableOpen_arrow = R.drawable.ic_arrow_up;
                resId_drawableClose_arrow = R.drawable.ic_arrow_down;
                resId_drawable_bg = R.drawable.rounded_corner_top;
                break;
            case 1:
                resId_drawableOpen_arrow = R.drawable.ic_arrow_down;
                resId_drawableClose_arrow = R.drawable.ic_arrow_up;
                resId_drawable_bg = R.drawable.rounded_corner_bottom;
                break;
            case 2:
                resId_drawableOpen_arrow = R.drawable.ic_arrow_left;
                resId_drawableClose_arrow = R.drawable.ic_arrow_right;
                resId_drawable_bg = R.drawable.rounded_corner_left;
                break;
            default:
                resId_drawableOpen_arrow = R.drawable.ic_arrow_right;
                resId_drawableClose_arrow = R.drawable.ic_arrow_left;
                resId_drawable_bg = R.drawable.rounded_corner_right;
                break;
        }

        Drawable drawable_open = getResources().getDrawable(resId_drawableOpen_arrow);
        DrawableCompat.setTint(drawable_open, tab_imageColor_tint);
        iv_tab_open.setImageDrawable(drawable_open);
        iv_tab_open.setBackgroundResource(resId_drawable_bg);
        iv_tab_open.getBackground().setColorFilter(tab_bgColor_unselected, PorterDuff.Mode.SRC_ATOP);
        ConstraintLayout.LayoutParams param_open = (ConstraintLayout.LayoutParams) iv_tab_open.getLayoutParams();
        param_open.width = (int)tab_width;
        param_open.height = (int)tab_height;
        iv_tab_open.setLayoutParams(param_open);

        Drawable drawable_close = getResources().getDrawable(resId_drawableClose_arrow);
        DrawableCompat.setTint(drawable_close, tab_imageColor_tint);
        iv_tab_close.setImageDrawable(drawable_close);
        iv_tab_close.setBackgroundResource(resId_drawable_bg);
        iv_tab_close.getBackground().setColorFilter(tab_bgColor_unselected, PorterDuff.Mode.SRC_ATOP);
        ConstraintLayout.LayoutParams param_close = (ConstraintLayout.LayoutParams) iv_tab_close.getLayoutParams();
        param_close.width = (int)tab_width;
        param_close.height = (int)tab_height;
        iv_tab_close.setLayoutParams(param_close);

        iv_tab_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extend();
                if(onEventListener != null) {
                    onEventListener.onExtended();
                }
            }
        });
        iv_tab_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse();
                if(onEventListener != null) {
                    onEventListener.onCollapsed();
                }
            }
        });
    }

    private void init_tabView(){
        String ori = String.valueOf(orientation_selector) + String.valueOf(orientation_extend);
        int resId_main;
        switch (ori){
            case "02":
                resId_main = R.layout.extendable_tabview_top_left;
                break;
            case "03":
                resId_main = R.layout.extendable_tabview_top_right;
                break;
            case "12":
                resId_main = R.layout.extendable_tabview_bottom_left;
                break;
            case "13":
                resId_main = R.layout.extendable_tabview_bottom_right;
                break;
            case "20":
                resId_main = R.layout.extendable_tabview_left_top;
                break;
            case "21":
                resId_main = R.layout.extendable_tabview_left_bottom;
                break;
            case "30":
                resId_main = R.layout.extendable_tabview_right_top;
                break;
            case "31":
                resId_main = R.layout.extendable_tabview_right_bottom;
                break;
            default:
                resId_main = R.layout.extendable_tabview_left_top;
                break;
        }
        v_main = LayoutInflater.from(context).inflate(resId_main, this, true);
    }

    public void setOnEventListener(OnEventListener onEventListener){
        this.onEventListener = onEventListener;
    }

    public interface OnEventListener{
        void onExtended();
        void onCollapsed();
        void onItemClicked(View view);
    }


    public void extend(){
        isExtended = true;
        ConstraintLayout ll_tab = v_main.findViewById(R.id.tab_cl);
        ImageView iv_tab_open = v_main.findViewById(R.id.tab_open);

        int resId_animIn_selector;
        switch (orientation_selector){
            case 0:
                resId_animIn_selector = R.anim.slide_from_bottom;
                break;
            case 1:
                resId_animIn_selector = R.anim.slide_from_top;
                break;
            case 2:
                resId_animIn_selector = R.anim.slide_from_right;
                break;
            default:
                resId_animIn_selector = R.anim.slide_from_left;
                break;
        }

        Animation slide_in = AnimationUtils.loadAnimation(context, resId_animIn_selector);
        ll_tab.setVisibility(View.VISIBLE);
        ll_tab.startAnimation(slide_in);
        iv_tab_open.setVisibility(View.GONE);
    }

    public void collapse(){
        isExtended = false;
        final ConstraintLayout ll_tab = v_main.findViewById(R.id.tab_cl);
        ImageView iv_tab_open = v_main.findViewById(R.id.tab_open);

        int resId_animOut_selector;
        switch (orientation_selector){
            case 0:
                resId_animOut_selector = R.anim.slide_to_bottom;
                break;
            case 1:
                resId_animOut_selector = R.anim.slide_to_top;
                break;
            case 2:
                resId_animOut_selector = R.anim.slide_to_right;
                break;
            default:
                resId_animOut_selector = R.anim.slide_to_left;
                break;
        }

        int resId_animOut_body;
        switch (orientation_extend){
            case 0:
                resId_animOut_body = R.anim.slide_to_bottom;
                break;
            case 1:
                resId_animOut_body = R.anim.slide_to_top;
                break;
            case 2:
                resId_animOut_body = R.anim.slide_to_right;
                break;
            default:
                resId_animOut_body = R.anim.slide_to_left;
                break;
        }

        final int old_index = tab_pos_old;
        Animation slide_out = AnimationUtils.loadAnimation(context, resId_animOut_selector);
        ll_tab.startAnimation(slide_out);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ll_tab.setVisibility(GONE);
                if(old_index != -1) {
                    ll_body.getChildAt(old_index).setVisibility(GONE);
                }
            }
        }, slide_out.getDuration());

        if(tab_pos_old != -1) {
            TextView tv = (TextView) ll_tab_inner.getChildAt(tab_pos_old);
            tv.setTextColor(tab_textColor_unselected);
            tv.setBackgroundColor(Color.TRANSPARENT);
            tab_pos_old = -1;
            if(ll_body.getVisibility() == View.VISIBLE) {
                Animation slide_out_body = AnimationUtils.loadAnimation(context, resId_animOut_body);
                ll_body.startAnimation(slide_out_body);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ll_body.setVisibility(GONE);
                    }
                }, slide_out_body.getDuration());
            }
        }
        tv_select_anim.setAnimation(null);
        tv_select_anim.setVisibility(View.GONE);

        iv_tab_open.setVisibility(View.VISIBLE);
    }

    public boolean isExtended(){
        return isExtended;
    }

    public ExtendableTabView clearItems(){
        ll_tab_inner.removeAllViews();
        ll_body.removeAllViews();
        return this;
    }

    public ExtendableTabView addItem(String title){
        return addItem(title, null);
    }

    /**
     * With animation when change selected item
     * @param title: title for the item
     * @param ll_subList: linearLayout for sublist that opened when click the item
     * @return ExtendableTabView self
     */
    public ExtendableTabView addItem(String title, @Nullable LinearLayout ll_subList){
        ll_tab_inner.setWeightSum(ll_tab_inner.getChildCount()+1);
        ll_tab_inner.setBackgroundColor(tab_bgColor_unselected);
        if(ll_subList == null){
            ll_subList = new LinearLayout(context);
            ll_subList.setTag("null");
            ll_subList.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }else {
            ll_subList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            ll_subList.setTag("notNull");
            ll_subList.setVisibility(GONE);
        }
        ll_body.addView(ll_subList);

        final TextView tv = new TextView(context);
        tv.setTag(ll_tab_inner.getChildCount());
        tv.setText(title);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, tab_textSize);
        tv.setTextColor(tab_textColor_unselected);
        tv.setBackground(null);
        tv.setClickable(true);
        tv.setElevation(getResources().getDimension(R.dimen.tab_elevation)+1);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (tab_pos_old != (int) v.getTag()) {
                    final int old_index = tab_pos_old;
                    tab_pos_old = (int) v.getTag();

                    ll_tab_inner.setBackground(null);

                    // Transition animation
                    if(old_index != -1) {
                        tv_select_anim.setVisibility(View.VISIBLE);
                        if(orientation_selector >= 2) {
                            tv_select_anim.setX(ll_tab_inner.getX() + v.getWidth() * old_index);
                            tv_select_anim.setY(ll_tab_inner.getY());
                        }else {
                            tv_select_anim.setX(ll_tab_inner.getX());
                            tv_select_anim.setY(ll_tab_inner.getY() + v.getHeight() * old_index);
                        }
                        ConstraintLayout.LayoutParams cl_lp = (ConstraintLayout.LayoutParams) tv_select_anim.getLayoutParams();
                        cl_lp.width = v.getWidth();
                        cl_lp.height = v.getHeight();
                        tv_select_anim.setLayoutParams(cl_lp);
                        TranslateAnimation anim_move = orientation_selector >= 2?
                                new TranslateAnimation(0, (tab_pos_old - old_index) * v.getWidth(), 0, 0)
                                :new TranslateAnimation(0, 0, 0, (tab_pos_old - old_index) * v.getHeight());
                        anim_move.setDuration(150 * Math.abs(tab_pos_old - old_index));
                        anim_move.setFillAfter(true);
                        tv_select_anim.startAnimation(anim_move);
                    }

                    // ...
                    ((TextView) v).setTextColor(tab_textColor_selected);
                    if(old_index == -1) {
                        ((TextView) v).setBackgroundColor(tab_bgColor_selected);
                    }else {
                        TextView tv = ((TextView) ll_tab_inner.getChildAt(old_index));
                        tv.setTextColor(tab_textColor_unselected);
                        tv.setBackgroundColor(Color.TRANSPARENT);
                        tv.setBackground(null);
                    }

                    int resId_animIn_body;
                    int resId_animOut_body;
                    switch (orientation_extend){
                        case 0:
                            resId_animIn_body = R.anim.slide_from_bottom;
                            resId_animOut_body = R.anim.slide_to_bottom;
                            break;
                        case 1:
                            resId_animIn_body = R.anim.slide_from_top;
                            resId_animOut_body = R.anim.slide_to_top;
                            break;
                        case 2:
                            resId_animIn_body = R.anim.slide_from_right;
                            resId_animOut_body = R.anim.slide_to_right;
                            break;
                        default:
                            resId_animIn_body = R.anim.slide_from_left;
                            resId_animOut_body = R.anim.slide_to_left;
                            break;
                    }
                    if (ll_body.getVisibility() == View.GONE && !ll_body.getChildAt(tab_pos_old).getTag().equals("null")) {
                        Animation slide_in = AnimationUtils.loadAnimation(context, resId_animIn_body);
                        ll_body.setVisibility(View.VISIBLE);
                        ll_body.startAnimation(slide_in);
                        ll_body.getChildAt(tab_pos_old).setVisibility(View.VISIBLE);
                    }else if(ll_body.getVisibility() == View.VISIBLE && !ll_body.getChildAt(tab_pos_old).getTag().equals("null")){
                        ll_body.getChildAt(old_index).setVisibility(View.GONE);
                        ll_body.getChildAt(tab_pos_old).setVisibility(View.VISIBLE);
                    }else if(ll_body.getVisibility() == View.VISIBLE && ll_body.getChildAt(tab_pos_old).getTag().equals("null")){
                        Animation slide_out = AnimationUtils.loadAnimation(context, resId_animOut_body);
                        slide_out.setInterpolator(new AccelerateDecelerateInterpolator());
                        slide_out.setDuration(200);
                        ll_body.startAnimation(slide_out);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ll_body.setVisibility(GONE);
                                ll_body.getChildAt(old_index).setVisibility(GONE);
                            }
                        }, slide_out.getDuration());
                    }
                }
                if(onEventListener != null) {
                    onEventListener.onItemClicked(tv);
                }
            }
        });
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        ll_tab_inner.addView(tv);

        return this;
    }

    /**
     * With animation when change selected item
     * @param titles: titles for the items
     * @param ll_subLists: linearLayouts for sublist that opened when click the item
     * @return ExtendableTabView self
     */
    public ExtendableTabView addItems(ArrayList<String> titles, ArrayList<LinearLayout> ll_subLists){
        if(titles.size() != ll_subLists.size()){
            throw new IndexOutOfBoundsException("Size of titles and Size of ll_subLists must be equal.");
        }else{
            for (int i = 0; i < titles.size(); i++){
                addItem(titles.get(i), ll_subLists.get(i));
            }
        }
        return this;
    }
}
