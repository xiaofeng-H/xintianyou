package pers.xiaofeng.xintianyou.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import pers.xiaofeng.xintianyou.R;

/**
 * @author：廿柒
 * @description：用户信息
 * @date：2020-4-5 18:10:51
 */
public class UserInfoActivity extends Activity {

    //个人信息属性
    private String id; //用户id
    private ImageView photo; //头像
    private TextView user_name; //昵称
    private TextView signature; //签名
    private TextView gender; //性别

    //其他控件
    private String imagePath; //String头像路径
    private Bitmap bitmap; //Bitmap头像路径
    private Button btnReset; //跳转重置个人信息页面按钮
    private ImageView fanhui; //返回上一级

    //当前登录用户
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化BmobSDK
        Bmob.initialize(this, "dd888bb2f4c6b543736e1169aa4e2dd4");
        setContentView(R.layout.activity_user_info);

        //获取当前登录用户的id
        getCurrentUserId();
        //id = "222";

        //初始化控件
        initView();

        //从数据库获取当前用户信息并为相应的控件赋值
        getUserInfo();

        //返回上一级
        returnPreviousPage();

        //用btnReset来监听重置个人信息事件
        gotoResetUserInfo();
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
        user_name = (TextView) findViewById(R.id.user_name_info);
        signature = (TextView) findViewById(R.id.signature_info);
        gender = (TextView) findViewById(R.id.gender_info);
        photo = (ImageView) findViewById(R.id.photo_info);
        fanhui = (ImageView) findViewById(R.id.infomation_return);
        btnReset = (Button) findViewById(R.id.reset);
    }

    /**
     * 从数据库获取当前用户信息并为相应的控件赋值
     */
    private void getUserInfo(){
        String sql = "Select * from user";
        new BmobQuery<User>().doSQLQuery(sql, new SQLQueryListener<User>() {
            @Override
            public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                if (e == null) {
                    List<User> list = (List<User>) bmobQueryResult.getResults();
                    if (list != null && list.size() > 0) {
                        int flag=1;
                        for (int i = 0; i < list.size(); i++) {
                            User user = list.get(i);
                            if (user.getId().equals(id)) {
                                flag=0;
                                //Toast.makeText(getApplicationContext(), "进入控件赋值", Toast.LENGTH_SHORT).show();
                                user_name.setText(user.getUser_name());
                                gender.setText(user.getGender());
                                signature.setText(user.getSignature());
                                //初始化头像
                                imagePath = user.getPhoto();
                                //如果路径为空，则显示默认图片
                                if(imagePath.isEmpty())
                                {
                                    photo.setImageResource(R.drawable.touxiang);
                                }
                                else{
                                    //将String对象转换成Bitmap对象并设置成当前的头像
                                    try {
                                        byte[]bitmapArray;
                                        bitmapArray = Base64.decode(imagePath, Base64.DEFAULT);
                                        bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
                                        photo.setImageBitmap(bitmap);
                                    } catch (Exception a) {
                                        a.printStackTrace();
                                    }
                                }
                                break;
                            }
                        }
                        if(flag==1) {
                            Toast.makeText(getApplicationContext(), "查询失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    /**
     * 跳转到重置个人信息
     */
    private void gotoResetUserInfo(){
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserInfoActivity.this,ResetUserInfoActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * 返回上一页
     */
    private void returnPreviousPage(){
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserInfoActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
