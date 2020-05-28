package pers.xiaofeng.xintianyou.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UpdateListener;
import pers.xiaofeng.xintianyou.R;

/**
 * @author：廿柒
 * @description：修改用户个人信息
 * @date：2020-4-6 16:52:15
 */
public class ResetUserInfoActivity extends Activity {

    //个人信息属性
    private TextView user_name; //昵称
    private TextView gender; //性别
    private TextView signature; //签名
    private String id; //用户id
    private String imagePath; //String头像路径
    private Bitmap bitmap; //Bitmap头像路径
    private ImageView fanhui; //返回上一级
    private Button btn; //保存按钮

    //修改头像属性
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private ImageView photo;

    //当前登录用户对象
    private User currentUser;
    private String ObjectId; //当前用户对象在web数据库对应的表元素id
    private String currentGender; //记录当前所选择的性别

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化BmobSDK
        Bmob.initialize(this, "dd888bb2f4c6b543736e1169aa4e2dd4");
        setContentView(R.layout.activity_reset_user_info);

        //获取当前登录用户的id
        getCurrentUserId();
        //id = "222";

        //初始化控件
        initView();

        /**
         * 功能：用户个人信息的显示，修改，回传和保存数据库
         */
        //从数据库获取当前用户信息并为相应的控件赋值
        getUserInfo();

        //用photo来监听修改头像事件
        resetUserPhoto();

        //用gender来监听修改性别事件
        resetGender();

        //保存修改的个人信息
        commitInfo();

        //返回上一级
        returnPreviousPage();
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
        user_name =(EditText)findViewById(R.id.name_reset);
        gender =(TextView)findViewById(R.id.tv_sex_reset);
        signature=(EditText)findViewById(R.id.reset_signature);
        photo =(ImageView) findViewById(R.id.iv_photo);
        fanhui = (ImageView) findViewById(R.id.reset_infomation_return);
        btn=(Button) findViewById(R.id.btn_regist);
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
                                //Toast.makeText(getApplicationContext(), "进入修改控件赋值", Toast.LENGTH_SHORT).show();
                                ObjectId = user.getObjectId();
                                currentUser = (User) user.clone();
                                user_name.setText(currentUser.getUser_name());
                                gender.setText(currentUser.getGender());
                                signature.setText(currentUser.getSignature());
                                //初始化头像
                                imagePath = currentUser.getPhoto();
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
     * 修改头像
     */
    private void resetUserPhoto(){
        photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showChoosePicDialog();
            }
        });
    }


    /**
     * 修改性别
     */
    private void resetGender(){
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(view);
            }
        });
    }

    /**
     * 保存修改的信息
     */
    private void commitInfo(){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final User user = new User();
                user.setUser_name(user_name.getText().toString());
                user.setGender(gender.getText().toString());
                user.setSignature(signature.getText().toString());
                user.update(ObjectId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Toast.makeText(getApplicationContext(), "更新成功：" + user.getUpdatedAt(),
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "更新失败："+ e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Intent intent=new Intent(ResetUserInfoActivity.this,UserInfoActivity.class);
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
                Intent intent=new Intent(ResetUserInfoActivity.this,UserInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 功能：修改头像函数
     * 码农：晓峰
     * 时间：2018.7.10
     */
    //显示修改头像的对话框
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_PICK);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "image.jpg"));
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    //裁剪图片方法实现
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    //保存裁剪之后的图片数据
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = PhotoUtil.toRoundBitmap(photo, tempUri); // 这个时候的图片已经被处理成圆形的了
            this.photo.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了
        /**
         * 此方法屡试不爽 每次上传的数据都是同一地址 而且也不知道是什么鬼地址
         * 所以我抛弃了它 鉴于以上种种恶行 也就不能怪我狠心
         */
        //        imagePath = Utils.savePhoto(bitmap, Environment
        //                .getExternalStorageDirectory().getAbsolutePath(), String
        //                .valueOf(System.currentTimeMillis()));
        //将一个Bitmap对象转换成String对象并保存到数据库
        ByteArrayOutputStream bStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[]bytes=bStream.toByteArray();
        imagePath= Base64.encodeToString(bytes,Base64.DEFAULT);
        Log.e("imagePath", imagePath+"");
        final User user = new User();
        if(imagePath != null){
            try {
                // 拿着imagePath上传了
                user.setPhoto(imagePath);
                user.update(ObjectId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "头像更新成功:" + user.getUpdatedAt(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "头像更新失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 功能：用AlertDialog单选框来实现性别的选择
     * 码农：晓峰
     * 时间：2018.9.11
     */
    //用单选对话框来进行对性别的选择
    public void showDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResetUserInfoActivity.this,AlertDialog.THEME_HOLO_LIGHT);
        //builder.setIcon(R.drawable.ic_launcher);//设置图标
        builder.setTitle("选择性别");
        final String[] sex_names = {"男", "女","保密"};

        /**
         * 设置一个单项选择下拉框:
         *      第一个参数指定我们要显示的一组下拉单选框的数据集合
         *      第二个参数代表索引，指定默认哪一个单选框被勾选上，2表示默认'保密' 会被勾选上
         *      第三个参数给每一个单选项绑定一个监听器
         */
        builder.setSingleChoiceItems(sex_names, 2, new DialogInterface.OnClickListener() {

            /**
             * @param dialog
             * @param which 此处的which只在本函数中起作用，在下面的按钮中不能调用该值，如若调用，该值会显示为垃圾值
             *              也正是因为如此，所以才定义了cursex，用来记录用户所选的which值。如果在确定按钮中写成如下形式：
             *              sex.setText(sex_name[which];会出现bug.因为which是垃圾值！！！
             */
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(ResetUserInfoActivity.this, "性别为：" + sex_names[which], Toast.LENGTH_SHORT).show();

                try {
                    currentGender = sex_names[which];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)//注意：此处的which与数组下标which不同
            {
                try {
                    gender.setText(currentGender);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
