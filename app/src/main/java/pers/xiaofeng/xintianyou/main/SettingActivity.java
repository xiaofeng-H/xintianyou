package pers.xiaofeng.xintianyou.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.Bmob;
import pers.xiaofeng.xintianyou.R;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * @author：廿柒
 * @description：设置页面
 * @date：2020-4-19 17:28:02
 */
public class SettingActivity extends Activity {

    //定义页面控件
    private TextView num; //账号与安全
    private TextView normal; //通用
    private TextView help; //帮助与反馈
    private TextView pwd; //修改密码
    private TextView resetNum; //切换账号
    private TextView exit; //退出登录

    //用户id
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化BmobSDK
        Bmob.initialize(this, "dd888bb2f4c6b543736e1169aa4e2dd4");
        setContentView(R.layout.activity_setting);

        //获取当前登录用户的id
        getCurrentUserId();

        //初始化控件
        initView();

        //修改密码
        resetPwd();

        //切换账号
        switchNum();

        //退出登录
        exit();
    }

    /**
     * 获取当前登录用户id
     */
    private void getCurrentUserId(){
        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id",null);
    }

    /**
     * 初始化控件
     */
    private void initView(){
        num = (TextView) findViewById(R.id.setting_num);
        normal = (TextView) findViewById(R.id.setting_normal);
        help = (TextView) findViewById(R.id.setting_help);
        pwd = (TextView) findViewById(R.id.setting_pwd);
        resetNum = (TextView) findViewById(R.id.setting_switch);
        exit = (TextView) findViewById(R.id.setting_exit);
    }

    /**
     * 修改密码
     */
    private void resetPwd(){
        pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,ResetPwdActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 切换账号
     */
    private void switchNum(){
        resetNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean currentUserFlag = true; //记录当前用户是否第一次登录,默认为是
                SharedPreferences sharedPreferences = getSharedPreferences("currentUser",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("currentUserFlag",currentUserFlag);
                Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 退出登录
     */
    private void exit(){
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //页面返回时，弹出提示框，包括确认、取消按钮，提示文字
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                builder.setTitle("确定要退出当前账号吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //清除原先保存在SharedPreferences中的登录用户的id值，并将记录当前用户是否第一次登录
                        //的标准设置为true
                        boolean currentUserFlag = true; //记录当前用户是否第一次登录,默认为是
                        SharedPreferences sharedPreferences = getSharedPreferences("currentUser",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("currentUserFlag",currentUserFlag);
                        editor.commit();
                        System.exit(0);
                        Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();
            }
        });
    }
}
