package pers.xiaofeng.xintianyou.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;

import pers.xiaofeng.xintianyou.R;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * @author：廿柒
 * @description：仿微信右上角功能菜单弹窗类
 * @date：2020/4/18
 */
public class PopWindow extends PopupWindow {
    private View conentView;
    public PopWindow(final Activity context){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popupwindow_topright, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w / 3 + 20);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

        conentView.findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //do something you need here
                Toast.makeText(getApplicationContext(),"关于功能",Toast.LENGTH_SHORT).show();
                PopWindow.this.dismiss();
            }
        });

        //退出当前登录用户（清除SharedPreferences中保存的id值）
        conentView.findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // do something before signing out
                Toast.makeText(getApplicationContext(), "注销登录", Toast.LENGTH_SHORT).show();
                PopWindow.this.dismiss();
            }
        });

        conentView.findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // do something you need here
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                context.startActivity(intent);
                PopWindow.this.dismiss();
            }
        });
    }

    /**
     * 显示popupWindow
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 5);
        } else {
            this.dismiss();
        }
    }

}
