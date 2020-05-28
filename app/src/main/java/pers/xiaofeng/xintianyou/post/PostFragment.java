package pers.xiaofeng.xintianyou.post;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import pers.xiaofeng.xintianyou.R;
import pers.xiaofeng.xintianyou.main.PopWindow;
import pers.xiaofeng.xintianyou.main.User;
import pers.xiaofeng.xintianyou.main.UserInfoActivity;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * @author：廿柒
 * @description：寄信箱页面
 * @date：2020/3/18
 */
public class PostFragment extends Fragment {

    //此页面头像信息变量
    private ImageView ivTouxiang; //头像控件
    private String imagePath; //String头像路径
    private Bitmap bitmap; //Bitmap头像路径

    //其他控件
    private ImageView more;
    private LinearLayout feige;

    //用户id
    private String id;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //加载布局文件
        return inflater.inflate(R.layout.activity_post, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化BmobSDK
        Bmob.initialize(getApplicationContext(), "dd888bb2f4c6b543736e1169aa4e2dd4");

        //获取当前登录用户的id
        getCurrentUserId();

        //初始化控件
        initView();

        //从数据库获取当前用户信息并为相应的控件赋值
        //getUserInfo();

        feige.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeigeActivity.class);
                startActivity(intent);
            }
        });

        //跳转个人信息页面
        //gotoUserInfo();

        //右上角功能菜单弹出事件
        topPopWindow();

    }

    /**
     * 获取当前登录用户id
     */
    private void getCurrentUserId(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id",null);
    }

    /**
     * 初始化控件
     */
    private void initView(){
        //ivTouxiang = (ImageView) getActivity().findViewById(R.id.info_post);
        more = (ImageView) getActivity().findViewById(R.id.iv_more_post);
        feige = (LinearLayout) getActivity().findViewById(R.id.feige);
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
                                //初始化头像
                                imagePath = user.getPhoto();
                                //如果路径为空，则显示默认图片
                                if(imagePath.isEmpty())
                                {
                                    ivTouxiang.setImageResource(R.drawable.touxiang);
                                }
                                else{
                                    //将String对象转换成Bitmap对象并设置成当前的头像
                                    try {
                                        byte[]bitmapArray;
                                        bitmapArray = Base64.decode(imagePath, Base64.DEFAULT);
                                        bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
                                        ivTouxiang.setImageBitmap(bitmap);
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
     * 跳转个人信息页面
     */
    private void gotoUserInfo() {
        ivTouxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 右上角功能菜单弹出事件
     */
    private void topPopWindow(){
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopWindow popWindow = new PopWindow(getActivity());
                popWindow.showPopupWindow(getActivity().findViewById(R.id.iv_more_post));
            }
        });
    }
}
