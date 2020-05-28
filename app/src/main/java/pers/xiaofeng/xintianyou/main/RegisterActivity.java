package pers.xiaofeng.xintianyou.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
 * @description：注册
 * @date：2020-3-17
 */
public class RegisterActivity extends Activity {

    private ImageView fanhui; //返回
    private Button btRegister; //注册按钮
    private User user; //新用户对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化BmobSDK
        Bmob.initialize(this, "dd888bb2f4c6b543736e1169aa4e2dd4");
        setContentView(R.layout.activity_register);

        //初始化控件
        initView();
        //返回上一页
        returnPreviousPage();
        //注册新用户
        register();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        btRegister = (Button) findViewById(R.id.bt_register);
        fanhui = (ImageView) findViewById(R.id.register_return);
    }


    /**
     * 返回上一页
     */
    private void returnPreviousPage(){
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    /**
     * 注册新用户
     */
    private void register(){
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = ((EditText) findViewById(R.id.id_register)).getText().toString();
                final String user_name = ((EditText) findViewById(R.id.user_name_register)).getText().toString();
                final String pwd = ((EditText) findViewById(R.id.pwd_register)).getText().toString();
                if(id==null||pwd==null){
                    Toast.makeText(getApplicationContext(), "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }
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
                                        Toast.makeText(getApplicationContext(), "用户名已被占用，请重新输入", Toast.LENGTH_SHORT).show();
                                        flag=0;
                                    }
                                }
                                if(flag==1) {
                                    user = new User();
                                    user.setId(id);
                                    user.setUser_name(user_name);
                                    user.setPwd(pwd);
                                    user.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String objectId, BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                                                Intent intent1 = new Intent(RegisterActivity.this,LoginActivity.class);
                                                startActivity(intent1);
                                                finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "注册失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}
