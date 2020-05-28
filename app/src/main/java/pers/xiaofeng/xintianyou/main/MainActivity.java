package pers.xiaofeng.xintianyou.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import pers.xiaofeng.xintianyou.R;
import pers.xiaofeng.xintianyou.letter.LetterFragment;
import pers.xiaofeng.xintianyou.post.PostFragment;
import pers.xiaofeng.xintianyou.receive.ReceiveFragment;

/**
 * @author：廿柒
 * @description：Fragment
 * @date：2020-3-17
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    //当前登录的用户Id
    private String id;

    private ViewPager mViewPager;

    private MyImageView mIvReceive;  // tab 收信的imageview
    private TextView mTvReceive;

    private MyImageView mIvLetter;  // tab 书信圈的imageview
    private TextView mTvLetter;

    private MyImageView mIvPost;  // tab 寄信的imageview
    private TextView mTvPost;

    private ArrayList<Fragment> mFragments;
    private ArgbEvaluator mColorEvaluator;

    private int mTextNormalColor;  //未选中的字体颜色
    private int mTextSelectedColor;  //选中的字体颜色

    //底部导航栏控件
    private LinearLayout mLinearLayoutReceive;
    private LinearLayout mLinearLayoutLetter;
    private LinearLayout mLinearLayoutPost;

    //onkeydown_
    private static boolean isQuit = false;
    private Timer timer = new Timer();

    private PopWindow popWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化BmobSDK
        Bmob.initialize(this, "dd888bb2f4c6b543736e1169aa4e2dd4");
        setContentView(R.layout.activity_main);

        initColor();  //也就是选中未选中的textview的color
        initView();  //初始化控件
        initData();  //初始化数据(也就是fragments)
        initSelectImage();  //初始化渐变的图片
        aboutViewpager();  //关于viewpager
        setListener();  //viewpager设置滑动监听
    }


    /**
     * activity跳转到fragment
     */
    @Override
    protected void onResume() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        switch (id) {
            case 2:
                mViewPager.setCurrentItem(2);
                break;
            default:
                break;
        }
        super.onResume();
    }

    private void initSelectImage() {
        mIvReceive.setImages(R.drawable.receive_normal, R.drawable.receive_selected);
        mIvLetter.setImages(R.drawable.letter_normal, R.drawable.letter_selected);
        mIvPost.setImages(R.drawable.post_normal, R.drawable.post_selected);
    }

    private void initColor() {
        mTextNormalColor = getResources().getColor(R.color.main_bottom_tab_textcolor_normal);
        mTextSelectedColor = getResources().getColor(R.color.main_bottom_tab_textcolor_selected);
    }


    private void setListener() {
        //下面的tab设置点击监听
        mLinearLayoutReceive.setOnClickListener(this);
        mLinearLayoutLetter.setOnClickListener(this);
        mLinearLayoutPost.setOnClickListener(this);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPs) {
                setTabTextColorAndImageView(position, positionOffset);  //更改text的颜色还有图片
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void setTabTextColorAndImageView(int position, float positionOffset) {
        mColorEvaluator = new ArgbEvaluator();  //根据偏移量 来得到
        int evaluateCurrent = (int) mColorEvaluator.evaluate(positionOffset, mTextSelectedColor, mTextNormalColor);  //当前tab的颜色值
        int evaluateThe = (int) mColorEvaluator.evaluate(positionOffset, mTextNormalColor, mTextSelectedColor);  //将要到tab的颜色值
        switch (position) {
            case 0:
                mTvReceive.setTextColor(evaluateCurrent);  //设置收信的字体颜色
                mTvLetter.setTextColor(evaluateThe);  //设置书信圈的字体颜色

                mIvReceive.transformPage(positionOffset);  //设置收信的图片
                mIvLetter.transformPage(1 - positionOffset);  //设置书信圈的图片
                break;
            case 1:
                mTvLetter.setTextColor(evaluateCurrent);
                mTvPost.setTextColor(evaluateThe);

                mIvLetter.transformPage(positionOffset);
                mIvPost.transformPage(1 - positionOffset);
                break;

        }
    }

    private void initData() {
        mFragments = new ArrayList<Fragment>();
        mFragments.add(new ReceiveFragment());
        mFragments.add(new LetterFragment());
        mFragments.add(new PostFragment());
    }

    private void aboutViewpager() {
        MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager(), mFragments);  //初始化adapter
        mViewPager.setAdapter(myAdapter);  //设置adapter
    }

    private void initView() {
        mLinearLayoutReceive = (LinearLayout) findViewById(R.id.ll_receive);
        mLinearLayoutLetter = (LinearLayout) findViewById(R.id.ll_letter);
        mLinearLayoutPost = (LinearLayout) findViewById(R.id.ll_post);
        mViewPager = (ViewPager) findViewById(R.id.vp);

        mIvReceive = (MyImageView) findViewById(R.id.iv_receive);  //tab 收信 imageview
        mTvReceive = (TextView) findViewById(R.id.rb_receive);  //tab  收信 字

        mIvLetter = (MyImageView) findViewById(R.id.iv_letter);  //tab 书信圈 imageview
        mTvLetter = (TextView) findViewById(R.id.rb_letter);  //tab   书信圈 字

        mIvPost = (MyImageView) findViewById(R.id.iv_post);  //tab 寄信 imageview
        mTvPost = (TextView) findViewById(R.id.rb_post);   //tab  寄信 字
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //底部导航栏按钮
            case R.id.ll_receive:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.ll_letter:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.ll_post:
                mViewPager.setCurrentItem(2);
                break;
        }
    }

    //如果在某个Fragment中对应进去了其他的Activity时，返回以后导航栏是没有之前的显示的，所以如下就要返回原来的显示
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == addActivityRequestCodeOfPage2) {
//            mViewPager.setCurrentItem(1);
//            bt_page_budget.setBackgroundResource(R.drawable.budget_pressed);
//            tv_page_budget.setTextColor(Color.rgb(255, 209, 0));
//        }else if (requestCode==addActivityRequestCodeOfPage1){
//            bt_page_home.setBackgroundResource(R.drawable.home_pressed);
//            tv_page_home.setTextColor(Color.rgb(255, 209, 0));
//        }
//    }

    /**
     * 回退按钮两次退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isQuit == false) {
                isQuit = true;
                Toast.makeText(getApplicationContext(), "请按两次回退键退出",Toast.LENGTH_SHORT).show();
                TimerTask task = null;
                task = new TimerTask() {
                    @Override
                    public void run() {
                        isQuit = false;
                    }
                };
                timer.schedule(task, 2000);
            } else {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                System.exit(0);
            }
        }
        return true;
    }
}