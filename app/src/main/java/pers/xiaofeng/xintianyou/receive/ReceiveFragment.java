package pers.xiaofeng.xintianyou.receive;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UpdateListener;
import pers.xiaofeng.xintianyou.R;
import pers.xiaofeng.xintianyou.main.PopWindow;
import pers.xiaofeng.xintianyou.main.User;
import pers.xiaofeng.xintianyou.main.UserInfoActivity;
import pers.xiaofeng.xintianyou.post.Letter;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * @author：廿柒
 * @description：收件箱页面
 * @date：2020/3/18
 */
public class ReceiveFragment extends Fragment {

    //此页面头像信息变量
    private ImageView ivTouxiang; //头像控件
    private String imagePath; //String头像路径
    private Bitmap bitmap; //Bitmap头像路径

    //其他控件
    private ImageView more;

    //用户id
    private String id;

    //定义此页面收到的信件变量
    public List<Letter> receives = new ArrayList<Letter>();//定义事件向量
    private ListView lv;//自定义listview布局
    public List<ReceiveofAdapter> receiveofAdapterList = new ArrayList<ReceiveofAdapter>();//事件列表
    private ReceiveAdapter adapter;//自定义适配器

    //用来存储信件的字符串
    private String[] title = new String[50];
    private String[] date = new String[50];
    private String[] isRead = new String[50];
    private String[] idDelete = new String[50];
    private String[] file = new String[50];


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //加载布局文件
        return inflater.inflate(R.layout.activity_receive, container, false);
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
        getUserInfo();

        //通过当前登录用户的id从数据库中获取该用户收到的信件
        getReceive();

        //将收件信息通过当前登录用户的id加载到listview上
        loadLetter();

        //实现listview点击事件
        listviewClick();

        //实现listview长按删除操作
        listviewLongClick();

        //跳转个人信息页面
        gotoUserInfo();

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
        ivTouxiang = (ImageView) getActivity().findViewById(R.id.info_receive);
        more = (ImageView) getActivity().findViewById(R.id.iv_more_receive);
        lv = (ListView) getActivity().findViewById(R.id.lv_receive);
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
     * 通过当前登录用户的id从数据库中获取该用户收到的信件
     */
    private  void getReceive(){
        String sql = "Select * from letter"; //表名
        new BmobQuery<Letter>().doSQLQuery(sql, new SQLQueryListener<Letter>() {
            @Override
            public void done(BmobQueryResult<Letter> bmobQueryResult, BmobException e) {
                if (e == null) {
                    //先清空ArrayList 避免重复加载数据
                    receiveofAdapterList.clear();
                    List<Letter> list = (List<Letter>) bmobQueryResult.getResults();
                    if (list != null && list.size() > 0) {
                        int count = 0;
                        for (int j = 0; j < list.size(); j++) {
                            if (id.equals(list.get(j).getReceive_id())) {
                                Letter letter = list.get(j);
                                //Toast.makeText(getApplicationContext(),"查询到相应的信件",Toast.LENGTH_SHORT).show();
                                receives.add(letter);
                                title[count] = list.get(j).getTitle();
                                date[count] = list.get(j).getCreatedAt();
                                isRead[count] = list.get(j).getIsRead();
                                idDelete[count] = list.get(j).getIsDelete();
                                file[count] = list.get(j).getFile();
                                receiveofAdapterList.add(new ReceiveofAdapter(title[count], date[count], isRead[count], idDelete[count], file[count]));
                                count++;
                            }
                        }
                    }
                    adapter.setReceiveofAdapterList(receiveofAdapterList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 将收件信息通过当前登录用户的id加载到listview上
     * ListView编程的一般步骤:
     * 1）在布局文件中声明ListView控件
     * 2) 使用一维或多维动态数组保存ListView要显示的数据
     * 3) 构建适配器Adapter,将数据与显示数据的布局页面绑定
     * 4）通过setAdapter()方法把适配器设置给ListView
     */
    private void loadLetter(){
        adapter = new ReceiveAdapter(receiveofAdapterList, getActivity());//初始化自定义Adapter
        lv.setAdapter(adapter);//填充listview
    }

    /**
     * 实现listview点击事件
     */
    private void listviewClick(){
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String receiveId = receives.get(position).getObjectId();
                String isRead = receives.get(position).getIsRead();
                String isDelete = receives.get(position).getIsDelete();
                Intent intent = new Intent(getActivity(),ReadLetterActivity.class);
                intent.putExtra("receiveId",receiveId);
                intent.putExtra("isRead",isRead);
                intent.putExtra("isDelete",isDelete);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
    }

    /**
     *listView长按操作删除事件
     */
    private void listviewLongClick() {
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {

                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                builder.setMessage("确定删除?");
                builder.setTitle("删除提示");

                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Letter letter = new Letter();
                        String objectId = receives.get(position).getObjectId();
                        letter.setIsDelete("1");
                        letter.update(objectId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(getApplicationContext(), "删除成功：" + letter.getUpdatedAt(),
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "删除失败："+ e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        receives.remove(position);
                        receiveofAdapterList.remove(position);
                        adapter.setReceiveofAdapterList(receiveofAdapterList);
                        adapter.notifyDataSetChanged();
                    }
                });

                //添加AlertDialog.Builder对象的setNegativeButton()方法
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                return false;
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
                popWindow.showPopupWindow(getActivity().findViewById(R.id.iv_more_receive));
            }
        });
    }

}
