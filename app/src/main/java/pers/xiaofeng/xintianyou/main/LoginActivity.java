package pers.xiaofeng.xintianyou.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Console;
import java.util.List;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import pers.xiaofeng.xintianyou.R;

/**
 * @author：廿柒
 * @description：登录（使用SharePreferences保存当前登录用户信息）
 * @date：2020-3-17
 * 测试更新github
 */
public class LoginActivity extends Activity {

    //定义当前登录对象的用户id和用户密码
    private String id;
    private String pwd;
    private boolean currentUserFlag = true; //记录当前用户是否第一次登录,默认为是
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化BmobSDK
        Bmob.initialize(this, "dd888bb2f4c6b543736e1169aa4e2dd4");
        setContentView(R.layout.activity_login);

        /**
         * 注意：此处可以获取按钮显示框等控件，但不能获取输入框的内容。
         * （程序在此处的执行速度是极快的，还没等你输入，就已经结束了。而只有到了监听事件处，
         * 才能保证你对界面的操作已经结束，这时，获取输入的内容，才是完整的）先前将获取id和pwd
         * 的操作写在此处，老是获取不到值，耽误良久。
         */
        //获取登录与注册按钮控件
        Button btn1 = (Button) findViewById(R.id.bt_login);
        Button btn2 = (Button) findViewById(R.id.go_register);

        //判断用户是否为第一次登录，是则继续登录操作，否则跳过登录
        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        currentUserFlag = sharedPreferences.getBoolean("currentUserFlag",true);
        if(currentUserFlag == false){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }else{
            //用户登录
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取当前登录用户的id和pwd
                    id = ((EditText) findViewById(R.id.id_login)).getText().toString();
                    pwd = ((EditText) findViewById(R.id.pwd_login)).getText().toString();
                    login();
                }
            });
        }

        //用户注册
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRegister();
            }
        });
    }


    /**
     * 登录
     */
    private void login(){
        String sql = "Select * from user";//表名
        new BmobQuery<User>().doSQLQuery(sql, new SQLQueryListener<User>() {
            @Override
            public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                int flag=0;
                if (e == null) {
                    List<User> list = (List<User>) bmobQueryResult.getResults();
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            User user = list.get(i);
                            if (id != null && pwd != null && user.getPwd().equals(pwd) && user.getId().equals(id)) {
                                flag=1;
                                //记录当前登录用户信息
                                currentUserFlag = false;
                                SharedPreferences sharedPreferences = getSharedPreferences("currentUser",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("id",id);
                                editor.putString("pwd",pwd);
                                editor.putBoolean("currentUserFlag",currentUserFlag);
                                editor.commit();
                                //页面跳转
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        if(flag==0){
                            Toast.makeText(getApplicationContext(),"账号或密码错误 请重新输入",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    /**
     * 跳转注册页面
     */
    private void goRegister(){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}

