package pers.xiaofeng.xintianyou.post;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import pers.xiaofeng.xintianyou.R;
import pers.xiaofeng.xintianyou.main.LoginActivity;
import pers.xiaofeng.xintianyou.main.MainActivity;
import pers.xiaofeng.xintianyou.main.SettingActivity;

/**
 * @author：廿柒
 * @description：
 * @date：2020-4-23 16:36:10
 */
public class FeigeActivity extends Activity {

    //定义布局页面的UI控件
    private EditText title;
    private EditText receive_id;
    private EditText file;
    private ImageView fanhui;
    private Button keep;

    //事件信息
    String id;//当前编辑事件的用户的用户id
    String subject;//所编辑事件的事件名
    String receiveId;
    String thingfile;//所编辑事件的简介

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(getApplicationContext(), "dd888bb2f4c6b543736e1169aa4e2dd4");
        setContentView(R.layout.activity_feige);

        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id",null);

        //创建事件对象
        final Letter letter = new Letter();

        //获取控件
        title = (EditText) findViewById(R.id.feige_title);
        receive_id = (EditText) findViewById(R.id.feige_receive_id);
        file = (EditText) findViewById(R.id.feige_file);
        fanhui = (ImageView) findViewById(R.id.return_feige);
        keep = (Button) findViewById(R.id.feige_commit);

        //返回事件监听
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(FeigeActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });

//        //显示日历
//        receive_id.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    showDatePickDlg();
//                    return true;
//                }
//                return false;
//            }
//        });
//        receive_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    showDatePickDlg();
//                }
//            }
//        });

        //保存信息事件监听
        keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取控件信息
                subject = title.getText().toString();
                receiveId = receive_id.getText().toString();
                thingfile = file.getText().toString();

                //保存数据
                letter.setTitle(subject);
                letter.setReceive_id(receiveId);
                letter.setFile(thingfile);

                //保存时，弹出提示框，包括确认、取消按钮，提示文字
                AlertDialog.Builder builder = new AlertDialog.Builder(FeigeActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                builder.setTitle("当前信件他人是否可见？");
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        letter.setIsPublic("0");
                        letter.save(new SaveListener<String>() {
                            @Override
                            public void done(String objectId, BmobException e) {
                                if(e==null){
                                    Toast.makeText(getApplicationContext(),"添加数据成功，返回objectId为："+objectId,Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"创建数据失败：" + e.getMessage(),Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        finish();
                    }
                });
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        letter.setIsPublic("1");
                        letter.save(new SaveListener<String>() {
                            @Override
                            public void done(String objectId, BmobException e) {
                                if(e==null){
                                    Toast.makeText(getApplicationContext(),"添加数据成功，返回objectId为："+objectId,Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"创建数据失败：" + e.getMessage(),Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        finish();
                    }
                });
                builder.show();
            }
        });
    }
    public void showDatePickDlg(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(FeigeActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        FeigeActivity.this.receive_id.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
