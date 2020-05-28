package pers.xiaofeng.xintianyou.letter;

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
import android.widget.ListView;
import android.widget.TextView;
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
import pers.xiaofeng.xintianyou.R;
import pers.xiaofeng.xintianyou.main.PopWindow;
import pers.xiaofeng.xintianyou.main.User;
import pers.xiaofeng.xintianyou.main.UserInfoActivity;
import pers.xiaofeng.xintianyou.post.Letter;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * @author：廿柒
 * @description：书信圈页面
 * @date：2020/3/18
 */
public class LetterFragment extends Fragment {

    //此页面头像信息变量
    private ImageView ivTouxiang; //头像控件
    private TextView userName; //用户昵称
    private TextView signature; //用户签名
    private String imagePath; //String头像路径
    private Bitmap bitmap; //Bitmap头像路径

    //其他控件
    private ImageView more;

    //用户id
    private String id;

    //定义此页面收到的信件变量
    public List<User> userList = new ArrayList<User>();
    private ListView lv;//自定义listview布局
    public List<LetterofAdapter> letterofAdapterList = new ArrayList<LetterofAdapter>();
    private LetterAdapter adapter;//自定义适配器

    //用来存储信件的字符串
    private String[] friendImg = new String[50];
    private String[] title = new String[50];
    private String[] date = new String[50];
    private String[] file = new String[50];



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //加载布局文件
        return inflater.inflate(R.layout.activity_letter, container, false);
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

        //getUser();

        //从数据库获取公开信件
        getPublicLetter();

        //将公开信件加载到listview上
        loadPublicLetter();

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
        ivTouxiang = (ImageView) getActivity().findViewById(R.id.info_letter);
        more = (ImageView) getActivity().findViewById(R.id.iv_more_letter);
        userName = (TextView) getActivity().findViewById(R.id.letter_user_name);
        signature = (TextView) getActivity().findViewById(R.id.letter_signature);
        lv = (ListView) getActivity().findViewById(R.id.lv_letter);
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
                                userName.setText(user.getUser_name());
                                signature.setText(user.getSignature());
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
     * 从数据库获取用户信息
     */
    private void getUser(){
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
                                userName.setText(user.getUser_name());
                                signature.setText(user.getSignature());
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
     * 获取书信圈公开信件
     */
    private  void getPublicLetter(){
        final String[] img = new String[1];
        String sql1 = "Select * from user";
        new BmobQuery<User>().doSQLQuery(sql1, new SQLQueryListener<User>() {
            @Override
            public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                if (e == null) {
                    List<User> list = (List<User>) bmobQueryResult.getResults();
                    if (list != null && list.size() > 0) {
                        int flag=1;
                        for (int i = 0; i < list.size(); i++) {
                            Toast.makeText(getApplicationContext(), "success get user" , Toast.LENGTH_SHORT).show();
                            userList.add(list.get(i));
                        }
                        if(flag==1) {
                            Toast.makeText(getApplicationContext(), "查询失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        String sql= "Select * From letter";
        new BmobQuery<Letter>().doSQLQuery(sql, new SQLQueryListener<Letter>() {
            @Override
            public void done(BmobQueryResult<Letter> bmobQueryResult, BmobException e) {
                if (e == null) {
                    //先清空ArrayList 避免重复加载数据
                    letterofAdapterList.clear();
                    List<Letter> list = (List<Letter>) bmobQueryResult.getResults();
                    if (list != null && list.size() > 0) {
                        int count = 0;
                        for (int j = 0; j < list.size(); j++) {
                            if (list.get(j).getIsPublic().equals("1")) {
                                friendImg[count] = "iVBORw0KGgoAAAANSUhEUgAAAJYAAACWCAYAAAA8AXHiAAAABHNCSVQICAgIfAhkiAAAIABJREFU\n" +
                                        "eJzkvVusbdlxnvdVjTHnXJd9P/e+kJsiLbUkSz6+IYyjRMcBDCh+URsIAsMPUTtIgLyJeQ0QmHly\n" +
                                        "kDxIRhAkfqKcl8BPlJMAToIApl58gROHMRxbsiX5tLvZbLL7nLPPvq215hyjKg815t6HFClHEsmW\n" +
                                        "nNXY2LvXWZc556hR9ddff9UU/n/+OP2zjx+zyZ+2jT0uYz21qZ66FUQqkkCkP1UZTnNOuGYcmMbN\n" +
                                        "02ncPi0VyAu6oSMpT6m7p2r+VWHx7te++ve/+kmf2yf5kE/6AH5Qj4eff3y6XvQ/vejT49z541Lr\n" +
                                        "afF6WqtTxkLZGl4FTMAd1UrOHSKJnBOaEiDUCtM4Me5GilU8Kzn3iCpJnCyOiGIIO09PrchTM/0q\n" +
                                        "Vr86wa+cffXvPv2kr8UP4vGvrGGdPvn8aZr46bH6E7PpiWo97YdE3yWSKlYKu1qw6rgbtRheFUFR\n" +
                                        "hZyEnHpSEpJWEKEaFIMyCnUyjAlPhuaM0qEerxU1KkqpPaMlqgmYYw7VytMs01co9SuT8itnX/3q\n" +
                                        "00/6Wn0/Hv9KGdbDx49PF+vu5wfy2ynp6cYK4zRhFJIWclKG1CEqlCpYBWrBAXMHqfQ5ozmDdCRX\n" +
                                        "RBynIg6Gs6tCNQUDvJDFyFmZAKsFMRBVRBO7mnBLmIO7YihVHKWSKLhVpOjTOtkvT7X8lbNf/VfH\n" +
                                        "yP7AG9bDz3/+NHv9WTd7JymPswpdSrgKk1VqmQAjyYgI5G6FpI5aBa+K14J7BRnJQ6bvOjQpVEEd\n" +
                                        "MGOslV2F6j1IJqWEu4HtUC8kjMmMMo24C6I9Ih1VFBEBB1DwFmm1QzwMjKlgpWDFcJevovyS5Po3\n" +
                                        "PvwD7sn+wBrW6b/x+Z+rMrwtam9rLWAVvJIUpJ2VA+6O+wjqIErXLdA0gGVqdbxMqIykDrTL5CGR\n" +
                                        "BJI5CceKsZmcqylRvUclISnhVvGyResWypbJKqUWkJ6UVmjKWAIVj6+ugbuqKE5CaN6w7PA6IR7H\n" +
                                        "B0J1qKX+ci3ll5/96j/8a5/kdf7dPv5AGdbpk8dHS4afM9IXtsipk2OJzMAN94qHOSGEJxJRHMcQ\n" +
                                        "RDNdHuhyB54o04SwZRgqkhJJe1wUdSNrpUsGVdjshMutMtUUmaJ2uBdsu8G319S6wZLgkkB7Ou3R\n" +
                                        "rKSsCFDcsOK4Z8wcd4hEwMAq6hVRRzTHRjCnFqi1Un16WrFfPN8Nf42nXz37RBfgd/D4A2FYp08e\n" +
                                        "H2Xrf756/kISPXKBKgIpoQ5qhEE5uFWwEbziUmOxyECEsC4JqpHdme0Y+sKwMNAOM2EcwavTJ6PX\n" +
                                        "+Hs3GbsRdp4R6Ui5x61Qd9f47hq3gnQJ7TpEBxIZVVAFRBkrlOKYOdUMM8cM6lQRm1BxUhY097jH\n" +
                                        "d9YK1Q33HVUKFc6E9Ivn19d/hadPf98b2O9rwzp9/Nbpcn/x85rzO8WHo2nqcHMkgWVHO1AErbHL\n" +
                                        "VTKOM5WC1xGkohpeRCXRpYyq42bUajhbFovCMGRS31GmiXHrTFsjCYhkvBaaY6E4aMqklBEp2DRi\n" +
                                        "ux1uhvY9qR+Q1ENVzAsihqMUy2EopTCWAghujtf4DMTp+g5NXWwQc9wFqxX3kcJIdYCEWXcmzi+O\n" +
                                        "Y/lr26e/+vSTXaHv/kif9AF8t8cbb/3YX9IsvyTaP1HtFtKAsHv8zskDv+AooAIqCUUxHBMHBElC\n" +
                                        "FiWrklICHHPD6kSWQs5K3w/klDGpZHGygrlSi1BKwr1DNdNlocvO0EE/OH1fSZ0gmoLv6jJJO0DC\n" +
                                        "OxWn1hpUQy04hrojAqKCi5PEAUMUJAMI7oZ7Qag3OFHM8AKILlB90qX+ncWd1xbbZ1//lU9skX6b\n" +
                                        "x+87w3r41k892b/z8G+p5rchLcwBFXJSlGZYBM4VFUQEqaDtP0HBFXdBUZKEYcW/hQGCoRRyErq+\n" +
                                        "J2lPSuExskgkAErgoSqkrHSDM2TokjMsnLyeyMuKJjBPmCXcBURwoJaKmeAVMIP2/YLHMYuiIlgt\n" +
                                        "mFVEHNF4zr1Q6wTumGsQt9WxGrSH0CHaLXB9Mhw8fKfbu/N/j+fffPpJrdl3evy+MayHb/3M6Z3X\n" +
                                        "P/ulnPr/AtEjasaLYWaIOKlTRMOA8IQmxYNcQkzCpCR+UAGJ1yaXWEzit9Ua/FNyuq6j6xcR2lDc\n" +
                                        "umYElVorVsDN6JIw9ErKAjjkinQTujAMxSahToKV4KysGbZbgH9zRSSjKSOSMMAcxC28p5d4rbeM\n" +
                                        "VipmE3jCPUERvDhYDfpCM6QcfJnoUZL0zvLg4ePF8sHf215++PsCf/2+MKxP/eTP/KWc/JdE02MR\n" +
                                        "QTWjLg2VO+JCTtK4H8AF1URKgVUoDsxeLHZ9kvAQ7g6kCI+1YHWLU8hdput6ck6IJtyUaSpsxpGy\n" +
                                        "HanjRLHIL5MkbAystZmMqRp1LJQKZkaZ5JaNr1C9YSirOBXPGek6NPW4OVYqFQOXAOstq5W2OdwU\n" +
                                        "LLXNIIi3jaOB77pOoyKQ5dVzfktU3lkePFhsXn74iYfHTxS8nz5++4jMl6WWJ4ErHCx+qxnOhNmE\n" +
                                        "SKVbOJockYyQ0SEjWTAzbKxRhulS8woCKKVYZFheMSpuW0g7ui4xdAu6PJD6Hqqxvbxme3lN2Wyp\n" +
                                        "ux1GBeki05xG6jgy5J7tZKBOtZGuy1hOaO6gCtNmohuWaNehXYerRLlnWEC3AIe621F2U3jiDC5g\n" +
                                        "dcRsRBPhbT1BTbjNWW7gNMHRHLhRNOGSqUgD+sBk8XrKV2q5/HNnn2D2+IkZ1ud+6p0n7vXLXssR\n" +
                                        "VqBOeBkxq4DhdSKubAGvSHZELbxXzqRFByloA5+MlJ0uK+SeYLkFXKFCYYP7SC1b0gAiRnm5pfMO\n" +
                                        "XQxsL69gt2NzfQXm5NRHKMod2i8Rr1gZSX2Qq9vtFZ3A7vIKukx1o447QHENa1F3JGXyYoWuVgyH\n" +
                                        "hziK1UqZLLBYn3F1xCP7E1HChQW+q1OBWhAfQQVVJXeJlIJgNQ8y1Zph+eTYVFExVDirbn/u41//\n" +
                                        "R1/5JNb3EzGsz/3UX/hLydMXjVbucPA64mWi1l1kRKXiXjEriNZw+RIZk+ZENyioINWpxVCtdCkj\n" +
                                        "qQ+aAMUx6niNWGHaXVOvz5FhYNpewPWOfjEgecVitWR3dYV2Hd3eAXW7Iy0S2i0gLxBRpu0l42aH\n" +
                                        "TxXJoXTwcQt9R7m6DBuWhG2uAA0vuNm0DFAx74KqONiH1Yqce3zocA923pnaYiSKGWXc4tOE1Al8\n" +
                                        "QhKkrORuCIK2eSp3C1xXDZ8MK4HdUlYkCeb1ix//6j/6z3/Qa/wDNay33v6Z0+2L4cuM8hizm9JG\n" +
                                        "8g5oeMgLYiOUAm5UbxlTw+TxB2gWEIdieAn2WjXhKdL9ZJXt5XPKeMV6mdhenrPoYHV8l24YYLdB\n" +
                                        "Fgek1UAlVA+SOjZWmV5eUCVB7ihjIfcDxSq220SygFBKQV2pSSgvX4CCaY/vrtBhjbkxnp+DV3bj\n" +
                                        "GEmBG54SpAHtlwz37zOpNg9jkc0alLplGq/xaYJawmsrdF1Hv1iCdLjGa8UDh5bJsMmoY4TMLgva\n" +
                                        "CdoB1K944S9++Ks/ON7rB2ZYf+ovPHl8ccHfOn/ZHdWd45PhLiRPKBkaaBcAt8BYbrjHQmoCVQUc\n" +
                                        "V5CoEEcaPhUoE6mOuBfKtCPXDYuucHz/DsNiAWYcHe/RHxxzdXWJ7yaOH95l2hUWBwdUlGlXOb8e\n" +
                                        "uXr+gprBuzXjOIVHsMq0HRGEzeaSTjMqiW2ZWKoyjjsmB+oW7feYysTVi49JKlAFnwrjbhNetJQo\n" +
                                        "66xWdMf30GGBpshavVbqdM00XmF11zgsBxX6vif3C1JeUFRxBC0FqSVKQJNTxxolqS7TLTKpc9wn\n" +
                                        "Si1nm6n86bN/9vQHIkD8gRjWn/h3nzzZbsYvj5McXV9B2RV0B+IpUmxLYErOGdEc9IAWhACo80Gq\n" +
                                        "KIgHGapgWvFRkPGS6foFWq/JyRkWiUev3Wf/+AhUOP2hzzBWp1w8w5fH2NVzjk8ecHF9xr3796gO\n" +
                                        "21rZXm/56IOv0w976OqAF88u+PjFM/L6Dl0/sNvusDIi7kxjYRy30BxnnQqaO86ef4Pl/iGrvWPG\n" +
                                        "6wtcCl6U6WrLxcszRJxp2qFUbCycb0fSasXy4IS8XuK2o4xX1HKJF8elxyRDJowl9WjKIAncg6kv\n" +
                                        "E1IdK06dpoYTlTwkNMNkE+M4UoudTcafvnr6/Teu77thvf2X/8w77/2D8qXL8y0gbDfGeOXIJqMm\n" +
                                        "jYcKviplQbrc8JQHoZlzGFR7mClKB2Wi1gvSuKPzC46P15zcOebk/j3GWtg7OsQFKBMPH96nW/Ts\n" +
                                        "SuHsxQWPTpaQV+RUuP/gPo6y2RYuzzeUccf19TUqmY+fPUO7TGGPj5+9ZLsbmWrl4vxlUB8NnNMw\n" +
                                        "zma7ox9mfqln3E1IEoo5Unfsri/YbTZMu4LVwvX5GcmhuDOWCt3A8mAF2Sk2heZLhwixkkg5kbrg\n" +
                                        "0kRAXYJ0LVEbNfNGXbR/T8HgWa2heC0TXh2K/MWLD5/+0vdz3b+vhvUf/fd/5p1n78mXPvwnI7tx\n" +
                                        "Q79QdqPz4usTm48TWnNkT5UA5SqIGqTAUCkLucsNtDtuYEVgV/DLZ9y5u2K5WLBaZ17/9Ju4Okcn\n" +
                                        "97i63nB0vM/V1SUnh3sUn3j0xptsds7u5ccM6wVDv+bocIVoopaJWgOfbDaXLLoujF0TZ+fnbK+V\n" +
                                        "Dz98zvlmx/nLC16ev2SxXjOs15gOQMKmgqtEMuoOLhGerFIgkpBpZDfuqNsN1xcXlO2W3W6Hawfj\n" +
                                        "lqvLMypOv7dPPlgjCi4DrgtimzkkxUUQMZJXpP0gFgvqoVZ1M4xg/r1AnUZKneIzzCk7/4tXH/3G\n" +
                                        "L32/1j5/vz743/uv/vVfmF7UL6SdcnBHsSEzrDO7XUFE+PD5Lhhrh9R2GBjBZQchmBonJWiQpZtL\n" +
                                        "/PKa5SDc++wJr73xJquTQxarBYu+p04Tw94BBycrCmuGacPxgwd02eiXS643Z+Q+s39wwvnLr9Mt\n" +
                                        "jrm8vORwb41QMS8cj3eY7BoZnS4n6kbYThccLGEQ5Xh5zPXdI0bJVIIZd5ypVsyCcTerIXdWqHUi\n" +
                                        "e8FMqdKjOVP6njQsuH75Eu037LYTRROLvWPKuGU6vyClRDo6JuUOE8FuBEFxjdyN6v5KjTS8F5rC\n" +
                                        "uCXAvBUJESFdkMzq4EbCvyT3fuSPXH70a//J92P9vy/M+7//3/07v5jT9AUdAzvpurJ83Tk6TXTr\n" +
                                        "TNkoL762o25BGvEXdTRIOJ0oKUdIcTN8t8PGa9YZ3njzHq9/5lO8+dnPcO/NN+n29hn6BUOfISkP\n" +
                                        "Ht1n2FvDONJnIXc9J4f7SM4cnRwz5A4348HDuyAJqYXD/SXr5QpcKOOGVepZysinXnsNtcT184+5\n" +
                                        "u9/z2c++wf7Bmn7occ0IiRStPKhIUAs4IgZEFqgyb5q5rKSoREE8Dz39sKTrO1KrG7obbsa42eDm\n" +
                                        "pGGBdgmloljUCt3Dg5MaYRxFcCcBSnXHimKjh8fyOL6UhYBnER00yeeH/TvH2/Nn/8v32ga+54b1\n" +
                                        "C//bf/bOsEh/WWzCxhFSRfcq69dG1g/AqnD5TefZu1vKpgk7vWV4VFKnaCdI6lCgnL8gbc/4oc+9\n" +
                                        "ycNPf5r7n/4h7r32kH61ZLm3JCdn2ScWiyU2jhyuFuhyTRbn4OSQw8N96rRhvV5xcLDHsBw4Od7j\n" +
                                        "+HgNNXGw6jk+XOO7S3og+cjhco8f/vRd7p7c5eLsgjfvH/DDP/QGh/srFl1PEoFqWLUo+4iB2s3m\n" +
                                        "iHNplQQcrw33zCJEiR9EbysG/UDX9yH+8yBKbbuDlEhdj4q2MmaNasNcAZWEhPALkPBU5tgUYN7E\n" +
                                        "QaPOSipoMkSjscMEUtbPL0/uv7t58dH3FNB/Tw3rv/0f/8t3VIcvlQpiyvZ6w6RGOqws7ld06Nm+\n" +
                                        "GPjo3cqz35yQjbdqvSHuZBVSnyELYmDnz1ivKj/6xx7z4Ed+HGOCOpKT0vew3lvRdYm+z0EeeuHk\n" +
                                        "cMlq6DjcX3OwyCyXCxTj5OgA216ztxoYOmV/0bG/3IsCc9mxGlY8uHeHg2XHG3fXnJwc0Q2JWnbs\n" +
                                        "rzoe3Dti6KP4jUftcTcZY6l46C5uCt3igXGaUBTwkMkQurGQxjTJtNc4f1GSKl3fYwKlGGWcmC7P\n" +
                                        "2Gx26GKNmOOlhKHiuORXvOVcGzVKrbgR5Z6obkXNUkL/HwqMFIlRVnKSt5fH9969fv69M67vmWF9\n" +
                                        "4b/+D59cXG2+fHW25fzZJbVUrq6vqTLRHU0Mh8q4Tbz8uvLNfzpy/i8mtGFJcUcVJAlJBd9tWKSR\n" +
                                        "vVXmrT/+xzl49Bq5XzGIcXh8yGpvxcF6yd5y4Phgj3WfUIw333gdr8bJ0ZJOjKV0dJoYtHL3zl0W\n" +
                                        "XeJwb8my7ynbLSeH+yw7uLs/cO/OPRZZ2Ftm9vdXpC5ojv39BYdHeyyGPthsjVKR45g7uynEe+4B\n" +
                                        "rgMJtf4J5DunRx7GBYBZdPZY0BaqStd1ARFcsDIybTcNLGRqidcjKWTXIrhEL+SsmXcXTBKmCVRI\n" +
                                        "yRDG5tQyJgnJucmxE4jjbm/3+0d/Y3v2/MPvhT18Twzrnf/mzz62qfzNF88uFh++95xpUxmnLZvr\n" +
                                        "a6SH5ZGTh47d8wVnvwnf+MdbpmcCGGINh7gDFRsvWcmOT7/1Wdb37/HgtQcsVmvW6yV39xbcu3+X\n" +
                                        "YehgN7K/6Flk4eT4gEf37uDbHYNWhtyhCA/uHSPqrKUy5MTheuBwb03ZjTy4s2ZvseBwuWBv1aNZ\n" +
                                        "6DLsxh3DYoGqklRIOZM0oSLkFB5LXDCfqMUppbCbmsSmCQydhoE82sqkdXfMmW0rBwaIdgGzAOfu\n" +
                                        "0SwrEtWBrEy7a2y3o253WIWkQ3in1EHq8Zk0Jj5TGhXhTRgpFFSmyCJTJESacpDNqmH7ou149M8P\n" +
                                        "B/f+1+3ZR79n4/o9G9YX/taT0zzkvzNe7Y7Ozy752tc/4qNnL5h2G2qtSDKydrDpuHzf+fjXR775\n" +
                                        "mxNSHKrhGplNKpXOrtlbLfnMH/5RDu69xsHhIQ/u3mEvr1jvGcvVklXfs522vH5ywr2TA45XA/eP\n" +
                                        "j8gpsewzh6seqcabDx+yv+pZdcK9+yesViuOVguGpCy7xMG6Z9EnugSqHddX11xdnrO/v0/Xup4b\n" +
                                        "GgeRwDFKCA7V8BI4ZTc502hsimEeHTl2o2slMtrZa3lkdd6APa1xwkI7Ez8yI7FWVMfZbXYwbmGa\n" +
                                        "EBmQfo3nBLkpZkUxD2CvMncpSeO24vmcJCRCWZoCRKNp4+a4FEEWwJ8fDk7++vbs49+TMuL3TDcs\n" +
                                        "P9V9efuuHJUOFktjuUp89HKDF2Poew7Hjn4sTDpx8f7E7j0jj061aEUXU9RHRDc8OH2Nk0enLFY9\n" +
                                        "xyeH3H14F6ewt1hwfHxIpkIx+jKwStCViTcePopyiCZgQSfCYgjcpZKRRU/uFJLQJSilsOw7Upql\n" +
                                        "zoAEOXt8cswNDg56Njii2Zu0hozV0jk+gl11rrYTm7FwPZXWVRPKBm0hysWpHmBemrndYC+Bit+q\n" +
                                        "SpsxBDqqEYoPjqBWzj6iMf9XpLpC6ypCYo7jD22jIVhTyUYWqcTxS/MhgQFLqFJL8FzmQtK4Hprl\n" +
                                        "yJJ+CfjTvxe7+D15rP/0a//2F8ez4c9fv+fYiygGby6F85cTu+2EmjJMgl5umc52bJ47V2cTu+IU\n" +
                                        "E9QTTNd4ueDBp17jjR//cQ7vPeL4aJ/j4zVHh/sscsd+Fo4WC+6tB1Zd4uH+knt3DnhwcszBasVy\n" +
                                        "MbDqO5ZDz2KZIn1PIYbLOaMqpBQAWlTJeRYq3zolx0IdEaIovEazQ4SJaB3zJJAjPAoSLfMVxgmu\n" +
                                        "xpHrcQpv5eHNhCBIPdqmWwxsVKc17ZTZjO/x5l1c2ussQlzXdYgou6sryrTFSPTDgOSmqlVI6qjX\n" +
                                        "1gRipJtza5jLgsYQDG2YzqtgkweDL4qk3ArXerr36A25/PBrX/mBG9YX/tmTJ1ff1C99/BuFq3++\n" +
                                        "w6eCWMf2onL2cmLcFnSa0DrCNHF9dcXlZeH6uuIOpS4wK+j4gpMHezz84T/EnQcPWfQdD+6ecHB4\n" +
                                        "yMFy4P7+mocHK17bX3D/eI+9IUX21gwq5cimNEHKkJIg4o3Fl1baaBe51RhFQg9/i30gNzI2OmOi\n" +
                                        "LqmpA01IDtJxXiSZxYSSqTWxm4zNbuRyMzFONQyjDQYxD/l0cEzSWr+iC0fQGxwWhOeNiqhJpC0K\n" +
                                        "0JLQfkHZjljdYrWSu4E0DNA2jIqD19B3WbT4qwqmLcLWED26ASg1Zay5TgdcJVrYspCjG/zJwWuv\n" +
                                        "/8r5B+8//YEZ1ttfeny0+VD/zvPfsMWLXyvYx5CbjOPyvPDibGJzOWK7yrTZsb3ecXVV2W6jOQA6\n" +
                                        "KALlgpP7d3jth3+U49cece/hffaXPXcOVzw8WvPoYMGj4xXH+0v29laknFkMiwhjSVHVIPpUv8WA\n" +
                                        "vv0xP3fbxHDL+8w/otFBIZoRbQYl2sR3GiRk+41kJGVqa6DYlcJ2qlxvJza7VrNrZSqgFYvbdzUM\n" +
                                        "ZR4GhkjzXhb/PnNfPsuyZ9m1kvPA1eV5+5xEXixC4y+RCLWpI80LBv3hFt9sU/OOoqhm0Iy7UH3m\n" +
                                        "uSRI226+rgoub+v9O391++GH29+pjfyuMNZv/O/ly+uD8ahOiXrhHPc9SQtdTow7pxRhGmHaGtdj\n" +
                                        "JXlBSWSFxeAkERITqzv7PPixn2T/6Ji7h/us+55HByvu7g08ONjnYMikTqJxAieldHshm0DL3aJ7\n" +
                                        "RcGmYJShseAS/+/Q5i2EYZk5enNBQzEhqo0ygCB+2gI3Q4DbQnhwQ8piNbCbjP39Jatlz3q1pL8Y\n" +
                                        "KWOhEs0c4jVYb20B1+N81ANw11LD42iE0HnWQ3hTaRG0YCj9es1q/4CLlxeM20v0akG3tw7jyxae\n" +
                                        "sUYSYJWbzmqrjpm1NrmEkIHUPLhGmERuPHj8dhCO9tLiy2e/C7z1O/ZYR48/98Xd+fiOjAbbjjoC\n" +
                                        "0pFrok7G9tK5uKxMW4PRoYQioZJJIgxZUDJlOue1t36Cw9c/xcnJPnf2Vtw93OPR4YJ7+2uWQ4dk\n" +
                                        "hdSFWiBJuH0EVYfUSMYa2ZgRTQrWLmzwQ3Nu3ySFHuYhInjS1iWtQIoBIcR0GG/tMo5EU0fL7NzD\n" +
                                        "E4R6IIx9t92x201cXW7ZbiuX11eMU+t0bp8TTRPeVJ/xfp9jXis6eMhAm+cKaTLmuERPobqQXMjD\n" +
                                        "kmkq+G4bn92vbozQJU6d6ohVvE7ReV2M7EJKHZJ7JKXwYsVuvKXInDXqjUf1qSI7O10dPpLL5x98\n" +
                                        "5ftmWA8ff/40afpSv+gWkR53QMybUocyCdfXzuV1xbZGHSeYHDWJuhqw7BNeJ45+7Cd5+Lkf5/7J\n" +
                                        "MQ+O97l3d59H+0vu7i3JfSKndBPyUpKbXZ2QNlrohtS+DX83CxYGV2uICVvd/5Y6SAnRDGiMFzK7\n" +
                                        "WfzbFZfbP4n0XyxAkFen1EqdCtNuYrsZubracXE5cbHZst1NTTAaAN1tziobEz5/ZguHMvNebZFt\n" +
                                        "Zuhv3teUHR6NrbWEaLDWiqa+Fao1NFrR6BhfYIIUibYBTdD3aJfRlKjWWuGsBneWCOCeAYxqlTpW\n" +
                                        "yq5Qiz0ejh7+9e3Z//fWst9RKMz96hdE8lFiRFDKlEiW8D5hNYDpblvAhVXXs02GaUXNMVGyRH0t\n" +
                                        "PXzIg9d/jEGEk3XPalCOh4G9ZY+1blEDihlJpeGyRjBG3SJ2dPNCYQetVWpmoWkXyCGjTM14IKQl\n" +
                                        "MgPXGosns2dTARNiVlp4w9kuw6iacqE2bzlN2DghNdL7LB692G5tPNFESk2j3jzorQETcySs4b+W\n" +
                                        "tYpFSGyndcPle+uMXq1WvHwe/Zbl4gLNC8gR3lBHWtfQzUbCmqRHSTmMT9zRGh9u5q1YXtt1a9/Y\n" +
                                        "eLWkepREfgH4c99zw/rcT739xEp9u0yZWjJ1F5nHsNKI2DkwThbn0XpJWndcdBueP7sAryQGNI90\n" +
                                        "6wV33/wJNDv7ywx1Yi8tWPapcTmJWJqGamwGwIonYVKiBIFjTWojCOqtD1ogpXzDI+grHdNuiltu\n" +
                                        "Pw2fWY2X3jDlQszLimREGzPuc1HZYTIjRmoVxrEwTZVSRrbbCzZXG6bWm3j98qwdulJrwRCQ7rZ8\n" +
                                        "IxYksXlrzE0xdaZGDlnEXuk5nClXJQ+Z/fWaq5fn1L6Aj4jf1g01J0gpeK4qmBbIivY52tJooarR\n" +
                                        "GRECaREhNS7MkC5FKEaRpG8/fPwnnnz41f/jK99Tw3JZfgk1VBOlJGrxYKC7CkYM3HBBhoFeB/CE\n" +
                                        "72WuL52xTDAZu3Hi4PCUqVuxHHqGBHePVhwerUgtpbeGYRwiYzG58Sa3XT31BnjOF9PnTE8i9EVh\n" +
                                        "Vlrxd2bAE+ZKKS1Tsjmgxo4NO27tZwROi8ZTp1q9vRZtoMdUKk/f/YB//i8+ZLvZ8vS9DzjfFF6e\n" +
                                        "X7LbjZRSmcYd5oH9NCfMouZnLngDItIwm0gOg/PQXEUC0mqB8c0ziGKx3mdzvaW6UTZXDKvhJszP\n" +
                                        "nrklsCQPSRFdQpKGt3bFc8LmYxAQifd6o+9zzhQJgpXs9Cl/CfjM98ywPvdv/QdfxKfTWd2Z3IAB\n" +
                                        "sRqy2AJaaHgFJolO3skcEwGpZB3pj++yfHhKlxTZXbF3cpf1EN3IM/5oghPEQtLmcouvQkAXXuMm\n" +
                                        "yyMybFdHVamtnzCRCPlbZD+3Nd9mJB6GF9lYZSojOc+XI0IZVoNIbN5s/k01qIVaJn7j1/85f/vv\n" +
                                        "/F90XWa3mzi/vOZ6tyV1PbtdoVYjaYqO6XECVabp6kaJEDyS4anD+zWiHYK1TDU88YwDfS79AMNi\n" +
                                        "QLPGRMDdho6mvKWNeHKIoRbBg5GCsnCR1k3u3yL5Vp2zwshOmxuPwrtEJivqpz/0p/7UF3/zb//t\n" +
                                        "L/7LbOZfCt7fevIfn4rYlxK6EFHmtDtGVo8okHNFUoDU7Wbi+kLZbZ2ryx2lVFQmBnHy4Yrl3TeZ\n" +
                                        "zj/i7mrg4b07HB3ttfqbBls+e5pGLIpLE6q1dNyg9ZXTrg9zy70ze7CnJlz5AAAgAElEQVQuMiBJ\n" +
                                        "hAogMzeDTruRPmdmcO5mbeTQrUHNw8/wxlA3hhxTqD4XbMFh/3Cf680WK8Y0GqVGNphST5kK2nUY\n" +
                                        "QnFHUh8ivFrRJFHqaTOzxlKxGi1vc+jzZnwVQCyohIa/EsZut2WqMe1Z80Ba9sFTQTPOFnMhXNec\n" +
                                        "2FgNMtabJ280zLz75qTFmpeOgnyKzY0+Xrz+6K9fvv/+bwvk/6WG9caPfP6LIvIE4iDCR1j0s1mb\n" +
                                        "yNJqYVNxdmNms03sdk5taW8vFe937D/6SVSdewdL3njtPnfvhVIhaWPPhRDREdfD3BBq4CAPYjPK\n" +
                                        "M01dCo0JbwbfDE5b1ictTLadANSWDMzst98UhOfrf8tQRJYmRoSvmwvd3tdAf9clHj26z/HJCf1y\n" +
                                        "yerwEOkX6HKPbr3P8qD9/7Ag9QNTDWZeUx8DRcyZrJ1jqVAmxmlqm0TBmonNB9Ye7jGra7u5ZrFY\n" +
                                        "syuFYb2MUAg35zbLeGZ4wU1L3a3NBX/2SgosNA8ZHjVIVZ0NcwH55fn7737ld21Yj5984QgpX0Jl\n" +
                                        "gSuQb0KLYOAjVq2NNYRpUsouUafUTqLipYCMrE7u4YsD9k+OuH//AQ/v32W5WpBShKMY5CFklTkB\n" +
                                        "CwOGW4NqM65U5z0Z6FxSDgFekzRrSsyF5FsZ7w1BQW3bNQw3LqAyG5Tc4Cr3Bt5pNIN/q9FFaJqi\n" +
                                        "TCMdqV/g3QK6NdatyOsj0mKPxfqQxeoQ6RYs1mv65ZpuuURSx2glQLtBqdPN+CKvUTgWTY09sBvj\n" +
                                        "iuw4PO44bnEXSjUkJ7Tvbo2vGcnNHz5j1Hmc07yZrMEFv0l0bj+hRRCZnxfM9LHeO/2r2w+ffldG\n" +
                                        "/rfFWMXOvmCjH4kkXCOkoJDaDCorYc0+JSYjUl133EtQBtVwm9jtRg7uvAk7Iauz3j+k6xfwSoHU\n" +
                                        "VGhzLchCq/g30NA8kQUTwA2IFUUl4SKYC1lTy/5iS1bxG/VA8vbe2mgHAWrMRBAXCh6DNpC2q4M0\n" +
                                        "jPfHzIh5EcyscUyGmxKCTm+YrSPnnj5XihueIpFwVYYuUaaBbIYZ5L2J4fCIMo3sNtfsri4Zd1ts\n" +
                                        "O+GTM5VrdFhCTi1SNLuIzANVZb0+4OXLCxbdwO7lS3SxwPtEmneBOaZR8NdZzTFLLGbq4+aTLco9\n" +
                                        "EpAET+H1hKAriMF1iB+tevnCGXzxu9nOd/VYp4/fPnL8f6h1WpQ6YnXC6oTSmgW84lZieIc54hrs\n" +
                                        "9c0Oj/gsGJoE3V9zcOcOd+4/4uRgn/Wg5DaKL8sMHjWq8+3cg4bRNgSjeRyd8VWUdOaQOHsx0GYg\n" +
                                        "NMO129AVh91C4a2qIIxG5mvLLNSbFQozSTkDaG8KBWJtGEtlNxnXu8L1WNhMxm4s0RndvsOcJuZr\n" +
                                        "mV7zgqYZVEmppxuW5H4ZnTZuTNvW0q9BCovNnzV/sGDVuL66QhymcQzDcMGnSt2OgfN07nWU30Im\n" +
                                        "O/OGIEpNDeuqzDT+XF+F2Xt5lMoen9w9/atn38VrfVePpVrfsTodFSsUmxAnWO9+SeqHIA+NAH5Y\n" +
                                        "CxHWQkarf0miIqTlkuX6hG59SDcMJIXqlVHCIOtc9a9GIQULzFwCadmig1ePmViEl+s04S2NDwsI\n" +
                                        "AD9TA0IYe+CimAGhNxezzflUuDETD0yiPheEmzpBwph0BvXN2Go1alND3AytnBUVqqg4pgH4g05w\n" +
                                        "SE1h4ETWhYPkUEQkJ3XGsFqTugwpsbm6QmoJOc+rpGkLyV3OLLqOyUJntX3+DEl94MGU6A7WpOVw\n" +
                                        "c8zfSiBXbhLDVqC/GWsgMXsLj3EAXlOjciwiE/XIlukd4Be/o/18N8Myqz8vBJgWq5TNFdPmnN20\n" +
                                        "pVRBm7Obw5POXJPFrE1xMHV8c41Q0e0labyitwnEYzhZDYuJpEspHmrKao67EmPLavOCwUHNKXL1\n" +
                                        "GlGNDJKbarNidYzFNqFaZfKJqVTKVCmlBFCeWXGr4cnifLF5RpeH/Dh8bwt5GNa6a8QNszZbvloA\n" +
                                        "bKfVMfWmuUFnL9ESk0Z+xAZ1v6FBpBXUVUIgGPLhnn61T7/cZ7OdbsL7jauc7WHGRCpol5i2L9me\n" +
                                        "f5Nxc47JRF4MZFoWTExinsP57BiiMK039jZXBsSd5IbUSjWj1BAzeqmz1/757+qYvtOTr7/15B0R\n" +
                                        "PXXxZjTcXExvaazRSlLeNFB9FHbRmN/UD8J6cAoTy3sPGY4fkLsVHWGo1YxireZWYwisV6E4FIQi\n" +
                                        "MWzfJDO5UDUIvpagNaVD0AtWDS8W9S+iCbaYMY1R77JpjItS4p45tRWu43udUuorPNUcBr3dqqR1\n" +
                                        "PbZwOYcPJAR8xS0I0JtG0tj94XabaPAVAvdVBcGrIDkiTWo6qiZdScqwWrLe22NWu0Y3zkzKRTjd\n" +
                                        "P9iHGoVqdyApebWkW60otVKsEDHebxK/b3/E8czn33AkRLOsCo3GD/zcuqrc7fT1P/lvvvOdPu87\n" +
                                        "hkLt+rcbP4Y6VA0F5Sy7QFPMIMdYDM5qkXE3rjfObqMMC+XgINH3hm3X7O0dkrqBvcMjZMgUPOqA\n" +
                                        "JtSqVPUA/w653kCdW7CJ3OjNY5EAUYobuQSZ57l50Bk/zaz3bsvQxd0kyF3U5ooHO+FE393NxQ6B\n" +
                                        "oLfU3OckArkBwhFWo7HUAqBhEjcvCMAWDL9HOtCMrBlVIze/VZ4yr+y3L3LQKGLKsBiok7QZ8bPM\n" +
                                        "uWFZc7quZ+h6rscdpIyXCS8FGUfSYiClOdmJJMC/g3XN88doyQ4egD2kRSE4tNLwmNhNhqjqbwO/\n" +
                                        "9Fts6NufePjWz5ymLv8skqjuuAo5DeR+SeqXpJwb4HWWnfDwaM3r95c8vN9zciRoZ6yHzN2jgcMB\n" +
                                        "rpi4uLyKFNdrDByzhFtMoRsdRnMma0PzCbV3EaGi4bU8cEk1Z/K5Cwa8yWRm+UydCuNm4vrFOc+e\n" +
                                        "f8Q/+Pv/J3/zf/6feP7R+ze44gaAN0wXhGfIXKTRRZVW7KZ5rnqrPjBooVqQdgs6L7UxATPjDTcz\n" +
                                        "vTRFqGmh8oav/LYUPgrj0miPeJFKeGVFyam7gQHfEkJb9WD/4ACIUeKOUXZbyq7EVJypYKKNbWge\n" +
                                        "eaZLWgS6sXHhhhz21smTckaztDGVM2HaegZIP3v0+MnRt9vRb/FYi1V6exb1zy3hljM5D4hA0sbu\n" +
                                        "6sTR3oK7dzOHh4lqBfyKi6srcioslgsk7XHnzQOGg/v47pps+7G4xO3Z1GheK0htM6PUhlFqazJQ\n" +
                                        "DUDvAeayEAASuJmTMI6Bxqrxtfc+5L1/+utMZcvF2TnbqwuuL86598CbgpIwBuFGCqy1qU2+fRd/\n" +
                                        "G55pXxohp9h8+x6s3qok5kYsSYpUAodFehveVoMUvREQtgnPNE/8qjeZwbarMAtMZ2Oajc9xJjPy\n" +
                                        "YslquU/ZXpM8jMvrlmwr1K0lAPlmw8w74JVvuz1fVWSeByGty0gTqrWlVNpImPBv60HfOfs2EP9b\n" +
                                        "Q6HIz3m97eJNBlk7UtcRYh0hiTN0mcP9xOHxwMHRQCkT2w2cXU50pZDF+eDrH7Mtd6h5w/p4jz4L\n" +
                                        "JkJpnSzJGs7BqSk8QW2VftMUVRTzaBtvC1MrSOVGTjJOE998/+u8fP6c8/Nz3nv/67z8xjdYrjru\n" +
                                        "Hhzw43/0D/PG6achaZt+1zI6ZqymqLWFTUE9zIs7S4t15sbmMlMrNdkrbPgNU+23SVfALW00nMFc\n" +
                                        "95sTgva9NH0+1tJ5n8mSRg8kberQFp6bgUXLVxizamJYrNsNDDYYxrS5DuWpZgbJ5K59j7x6fHJj\n" +
                                        "xOA3XlR1Hn8+69Ksldca7mrvj4WQn+O3M6zTx2+fOuNjMChNqtEZXa90Q0cBqilJjdVK2dtLrNdL\n" +
                                        "Fqsl07hhWG44WvWIOX2G7eWWbl94+Oh1dtMVedWjHvbu1SkSoSGZkWtlEiVpuNrIvqM+Z9QY5ErM\n" +
                                        "dp/MuDh7yb94932+/t77XL58ybOPPgqVpBjD0PH6o4c8enCXH/2jfwQW6/BMNNuwV3ZnbViJitC1\n" +
                                        "VHr2BQmRSFrajS7i+ebxQlFjbSpzJCDVpvAETXJj3sYIvBL6ErG5gNDRu2ISig0jJhuaGaLWPqdh\n" +
                                        "QYKGqW1zqAa1IhXcJob1kn5Yst2cU6hN+XCGPzf63FGJDiVr6oXgqgTmpGO2OAk4oA4uFsN3Z66y\n" +
                                        "WXckIynCt/jj088/OX36d7/y9Dt7rFS+ELsvkL8SvMr+Xib3le2UmEboMvS9MvQpMFdr5eq6nqPj\n" +
                                        "FUk6+iFxPU0cHp1wvtmwtwgBv2m7pVrjwaw6RZTJo3Om1BD3FVE0OSrtRAw2m8LV5QXXF5fNsN7l\n" +
                                        "a+++R9lu6LKyWC45Ojji7t0j/ti/9ie4e/cui8WqnZzdsPwi0nRY0cIlIo1rqiTNrYFBbgFow5Rx\n" +
                                        "l4nbyyWvYKO51GZNseot+wkPEfr22ZrDI4Q+TJQ2gzS8uahGN7XMM3iUZm5RBH5FlRECvYQKWBnR\n" +
                                        "bojqgsb1nfXzZXvFy689pds7YXHvDv1iINGMVW55q1m9EbfCu6Veop7KrRBgPvuWl4QGjnd4hYn/\n" +
                                        "FsNS1Z91N7zdVVSlsl727O1HmKjXRilOztD3GdHYzbWAeGUYEl23IKcVz59dMJ6PXD9/SXeUYkCZ\n" +
                                        "RNrqrZXSTLHqN1lhMiFjVAO1FjabQK6MI9/82tf5tf/nn/Dimx9BLSyXA32f8Uk5OTrg+PiYo6Nj\n" +
                                        "fuIn3+LRG4/I2t2k5hVhniE/KwjcEiaOpjAw9YbeW8b4Km0UuFNvSyA34KQ9bwF6g4bxljFKu5aC\n" +
                                        "p6Zwra9+YtRA4y5l0SVjNVyUtxFON9/xyuPVbDK8YMyBVxFy38Vwtsnb9e7oFvuIGnXzku0HO7o3\n" +
                                        "HlJXQ9tA87yH2QtKYyYqqGE13bbP4XGTK7eGveyGSFXlZ7+jYZ0+fvvU3E/nXl2XaHzY7zOLTphE\n" +
                                        "qR60f9clVA2xEZsE6zIJZ7XsSP2aTisqA3uHh+TFiuXyiMyOzALoGgCMYWJFEsmiiFrF8E6ZzMEK\n" +
                                        "adahl5Gzj5/zwQff5OWzl1ycvyCLUnbX7B0csHf3Afvrjs/9oc/wR/74n6TruwgjcstSq0u7Tduc\n" +
                                        "4UlLqytFosbYORSirpiaOc5kbxhlc1iv0EjcwNiYAq0V3CoJpTAj7iAbDYn6J/MoI6NaCS1ZCWGj\n" +
                                        "tNfITeo036ppNqJ0g7G8ebYqDct5oVvuMXQdu2nXXG1Fs9PtHUYidH7G9uycxerebVc2M568bfqA\n" +
                                        "FP2J4XKCphAwCZiS48qEwjSoisenT54cPf3KV86+xbAq+iTbDvcRsUIWZb3IDMuM6gK3HsmZIScW\n" +
                                        "C2cx7OiHRN8nuqQkdXLO9IsFWeFqdwXLnrTcp4qw22wYdaJvXTHzTnSLFkPxmMU5mUGJV4wK49WW\n" +
                                        "r7/7Pr/5j/4xH37wAVa2HK4W7O3v0w8dw5D41Juv8enPnHJ6+mYr78hNwdxaGLshphoQNastHMVO\n" +
                                        "TCnazQWPjqDmcW4SqBaSmDGT3zALN9oxs7lqENUIFyIU1sY7+cx3NUOc9VIStI66YCl2QowWjx4B\n" +
                                        "Xgl9rSvx1mvdgj/cK8vVAWeeyCSSZEyMurlGu47FyR265QPG80tss0EWyziUmXSVlk3KK+cnEtSQ\n" +
                                        "xvQbUb/RzIWhtUwKpUeeAL/8LYaVkSfiDrWiVukHGHqlujFNFVVlyD05ZVbLyt5aWa8zq/UClYrV\n" +
                                        "HfiIaBcF1TzACGV3Rb+/Zr8/ZkwDvWRmABwXQ6gWQLGYMdVYRC/CNI38+q/9M371H/xDNucv6BTW\n" +
                                        "ywV56CkYr9+/x+tvPuIP/fBnOTg+QVOTN7vjswJDAvDKjURmJkStlYpiOIa8koYbHvfi0TkYzns7\n" +
                                        "sFAb93kTHuaWsBstV7PhV+IVjS1j1tl7LUitUErzoE1ZkdpgEQvsdlNo92bYzZPNHmy+jmZRfumH\n" +
                                        "BZr7SF1TIusQxmzG9uqK5WpFXkbUUBfMGpaLFPC2+DxnizfkMC0Ehqo3aTMduZFKkL6TYblOPx2I\n" +
                                        "P+7Xsloa2gk2TRS5wHNiWAwsho791YKDA+fwYM1iCbtdSD68bkhUdL1iLMrVOKLPv0EF0nLg0WRz\n" +
                                        "i16src8CsyBLC85E4LVajQ/ffY8P3v0au801UkcW3cDhwQHduufw8IC3fuJHePT666xX66aucExi\n" +
                                        "5tN8M4I5bQ7aohmPt7kIHjs+lF1OJYFUkoGpIu3OYw29t6jWOl4aO/8t9MBtse1mUZq6vtll9Aim\n" +
                                        "WhnHDeNuZJwmbCxoLZAU63pEM11qdyKTieo1MmiVW8168/h4bAAp0UOYPTZ5t1gxHJ8gpkxlohsy\n" +
                                        "42aLrPeoqUdq4wCRRrw2zyczv9VAenNfgR8dKY50rYNaJOQ5tBov+tO3jgp46/PvnO7q1SlWSd3E\n" +
                                        "3ipxtCd0Wah1opSJlAp9VpaLzHo9sN4T+iGDT9huZHd1zvXmGXVaspqOqbsDuhR3Lt0/OkGtsLm6\n" +
                                        "YH84CR27314Yx4JOsDAuM+P6xRnf+OB9PvzgPcbNJcsuk1YD2id+7Cf/MA9fe527J3fohgUuOfay\n" +
                                        "leguDiROkqaOIPCMiN+C6QbEZyAao4QEtbjtm8zAVF7B6W0xxeaLfhPJ2mYJbxRjsaXlKhadOG5Y\n" +
                                        "LZRpy3R5yfWLZ1y/eM52V5maMVotdLmj39+nO9hH+wGzAS+CNO0Y5VbKIlJu6nvzDRXcnYP9I56/\n" +
                                        "eEG3PiANPT2FOk5kE8bNjuFgTTFrrXTNI79Ch3yLJ8ZuhIZ1jjBFm0Azrse8yRB/fPr4ydHTr37l\n" +
                                        "LANM1Cci0Qy6HBJHe87evpBTpk7K5OAJcqoMvbJadiwHI1Gwes1ue8bu+pzri5fItGWaJnYenmA3\n" +
                                        "Ba1w/fIZm6sD9o6Pkbm8MGcibb5mcdrwipGPv/4NPnj6Hlfn5wydsthb8eD0U/zwWz/CG6+/xnq5\n" +
                                        "JuUFXhMmqa1uGyHkEa70Zjx29AIi7bl5XKM1MK6tgGs15k15y7Qa7ohxS7fY6sZbNC5InKY4bbRh\n" +
                                        "44dE2oxjd6RM+HaLPT/j5dfe57wU5PAePDhBtQ9d/XjN9vKMzbNvMDx7wfLBfdLeAfQddbu7OX7q\n" +
                                        "bFi0A2qx2aNVvstBJ4hBnxdIlxkXTr/YMW6u8GqkPod2y4Q5Q52TgjkdTk1NO/uziiH8v4y9Waxm\n" +
                                        "V3bf99vTOecb7lgTq4pkFdlki+puqpvqlhRJiVqWnEQZgCgGDDtOYsnIQ4AkgN/yloc4CBBDCOwA\n" +
                                        "loIEiOM8WEYMG1GQQJGl2KARKWoNltlstboluskqTjXf8RvOOXtYeVj73FsdyUgKYLNBsm7d+519\n" +
                                        "1l7rv/6Dq59bfVnQF1bZEQZm7seBX/L6ssiXrBG6Dq7sNBzudSx3GoxrlMcehSFraoR3hsZmvLdg\n" +
                                        "B8yYKHkgjmuGzYYSN6yGgSSF1dmKw4MbrDfndK7h9JN7XH3hJhJmFMCLrctsW6c3YZDC+vFT7n/n\n" +
                                        "fc6Oj2mc5YXrV7j7PZ/lpVc/w9UrV7HBI86rMb8t6ktqVYyZq8jVUoWgUi49r8rU+BSKycoDA2yR\n" +
                                        "i2kwi8E6p+EGZqpIgin6315QCEWtghLTTFDXTEVU1Frfbim6Wok5MZyd8uTTD1jvHbK4+QVksY/x\n" +
                                        "DYIhpQHT96T1lrT3AttnHzI+fsKuZFy7o9HCRZvl4qhuOR5M0odv7MX11cxmYArOeKRP+E1mPp8R\n" +
                                        "m7nyJEXIprYGF4v+6ZdOkoZcYRitKkLWNqD2XkW4CHl3TNsRB6ZcHixbhi91IXKw9Fy52nJwOMMH\n" +
                                        "laD7BDJk0iAICSk9xjtm3YzQtsTe0Z63uMYiNrMdCsSBdVlR8hYXZqQsarSKI257Gt9dNIelaKOo\n" +
                                        "UidYn51z9OkDhs05uJEXbr3AS2+8wY1XP6P0EefI4inZky0YUu2QNM/IWdHte11dFAOpDgfPX2fT\n" +
                                        "XyWrbL2IetoVdIdoBFUKm0vfBr3/6kTEJQPTGsXA7CSpV5OrSmEuxGwppyecHx9h73yB3Zc+R5gd\n" +
                                        "QFAroZwEO/SUZgthhQmG0nhK85Dj0yfsLK2GSFmLWIuVel2L110k+lCV7jMSGjXCTafH+LmGb5re\n" +
                                        "Mdu/xrbtEAZcTuRpAsRR5U9aacVeagXqNYiZNhe6XpsGIWMUIzS4qdf8EtQey7tyZ2cGV/cbrl09\n" +
                                        "YHGwiwjE7UBOo+JNacSi6wNrWnxomc1mOBvolj2L3XNySfTbzNnQ8snDUzarU8YxU9KW+e4eQ39C\n" +
                                        "GnraXfPdfcvU7BZtjDOW8+MTDnYXXL/zMlduvsx8toNxnlQzoaMaEmBFMYUElBxxE1nO2Hor6UK1\n" +
                                        "1KN9MazJNKILuSi8obrGwsRfLQY1W6u/73KHqJeHoEttZ9DQ8ImbVYmKE4ug9D3j+TPSwS38Gz9I\n" +
                                        "s7yJs4a4esZ49BSaGWGxS3RuGljxIgpGl8zq5Jj5vCHMOvDaD5GMijnN5ExYh4kSEbNQrn0ZcFmw\n" +
                                        "JdA0kDYrWr/HMF13IgpykuumwNb2ZALqtEc1U8cleghNsZc4muiWQV9TAZE7Fwera/Ld5Twx39uh\n" +
                                        "3ZnRdQtSTIyMDDkSx0SJESkRSQYk453Huo5iEsUsCPN9Dv2cVXR865v/jNOzLWIb8vlT5rdehrbF\n" +
                                        "lo5hc8qcG0BQRib1my8QDZQx8ujjDwkS2X/hZa6++ArtfH6xtBULOWuMiA2KsThjlUBYacxDNT/z\n" +
                                        "1pCzjuxVdcilo4tDTJVKKCH9omrqP7eUADElgvdamUrSG1Um/ynB1mpY619teZRiQxFyHDCrU44G\n" +
                                        "Q/vml2kOXiXHkU/+4d/lo//7f8P1x4gPzO6+xZ2v/tuY5QE+RXKaQzuSZgeUMbM+f4wf1yz2DxUQ\n" +
                                        "LQZrMtYKuQ6exgiuQPIwa+eYnNTDoZlh2n1cPIJNQ15apFoITC/XBMgqbqU/f+Z5mKz2wliEgFBZ\n" +
                                        "G9PiS8xUqe8C+Nd/4M9+yXJO4wydD3jfYl3AZOpKRUhRiWPeZshR79ZiGDKsBhhGoUhHbpd8+/5H\n" +
                                        "PHx8gum3OImcPHpMWF5jefgCBSHMFkxzVhEuJous55Xjh08xBRYHB7z0+huE2RKxnlgMfqpQAlYs\n" +
                                        "YwZnCskoau2Mml2kpFdacBC84NAgFW+1shhn4IKrXqsWdekqNQYXwWTURGOCKOqb6Yz2KWL1YU7O\n" +
                                        "gcrrStQzpe6AcWSzPqd55fO0N16jOM/7f/evc/rur+IlI87ijeDu/Q7fefYRN37iLzC/dgfnt6Qw\n" +
                                        "w4YGFzp1i7GaT5GLfr8T3GHqIFE5QRjrcb4lGAVmvbHE4QyXIyIbwnyXwVlEQ4wUUnlujzmJVcxz\n" +
                                        "7ItpBLaiZrqV0H0J2BldUBvg9g/8+Jd8iau72a8xuVFucx6JKekawQUKtpqzatqVdepyF+OIi4kY\n" +
                                        "DUM0jMny4GzN/Q8/hH5DHBJ56DF+YHv0iL3bN5ntHZJp9D6uqLMwKUcMuY/kmGm6llsv3cLtXyW7\n" +
                                        "hoQeqFikNtVGufKSiVJ/YKOh4K4yJFKBxulEqJrES+VOpctdgoui1axUrEYqTUapMPaCpjs5EtsJ\n" +
                                        "gjBVNDThDRUolFKQBKWMSBrZ0NDdfQu7POD0d/4RR9/4NSQXhsnuyFnKsqMZTzj//X9M+9V/DxMC\n" +
                                        "wTtK6MihIfuGbjnTalXXLjK1gPVqK+jPa4zV9NhhizUBGUZyXOFcR2ZQXppTwubEReWySbioQlIP\n" +
                                        "0/R8hLo0FC4m3+n3TTZNWIspft/nIl/K4wCmAe9x3iMmKy8qCSUbUlI6bhs6QggkRsY8EGJE4siY\n" +
                                        "tnx8vObb9+8xbrbqaNcnBCFtB0zryXHL7PB1jaItDdaVCSVVXkglOi12O2RYsnvjJrjm4g0Vk8jF\n" +
                                        "kbI6wUyydXWsU3aVs2CMw1rorCV5Q8qWEjydN/Vw1HfducoeHfVKLpCMXKxKPDrWR2OgMRc9BVZZ\n" +
                                        "id7owZ6uWY/SSuzEySoJklCGkXJwhWb3BQiOR9/6XQKZaJcYOWMAmhTZrhPtriceP6QMW3wIiPcY\n" +
                                        "D94minM0vtNIFOqgIHo1TT0k055PBNfuaJ60n2H7c1w2RBOx2eCHyOhbhWQwOllr0dHpT+c87SFF\n" +
                                        "V13qJJWYJCEW5ccpIq+fW7YOZw3e2R/3Wbibjcf6jrbrCE2j/1Hs6fs1w3ZD6nsaV2gaRzcLWGdI\n" +
                                        "eSBvT3m2HviDh095eHyE3zvk6nKfuO1ZffQpqw/v62K5CNZ2nD58AE1md3+JQfVvhYgVXQL7xrN7\n" +
                                        "/QV2D6+oZMl4ZUAUpSqb2kfkWON84cJsI6ZMSRpGoD+co2sD8y4wdolF1zAPvl49uuK4oKZMy+6i\n" +
                                        "VBK9FrWiGmtw2apo1Ez40XMEwOrfqTeRKGkw5+qNlcgx0ty4jW8WZN+QxjVtt+TaV36aD7/2D2iO\n" +
                                        "PtapNiesF3La4kvEiycBhhYThSZ4TBMww3jBPZ8W7M//slWLYEMgV5sBvfPzBfxRhgG6+cUo891k\n" +
                                        "v2nxrtf/BdN18nd4jiWLgcvV3KRPdAB3vTXmLjZgmxbnG1xddsaxZ+jXDP2WkiIWQ+OgawNt60Ey\n" +
                                        "T88Hfv23/wliwSaD7QfcPJDnM5bXrvP67Ze5f/8paRg5f/oI4SkHr7yAIVxA1lIKpVjlwns1yTXS\n" +
                                        "VIsi1erlIox1VaZ0ZWUL5JxZr9eMvRrppvWKkjZ6LTYNs50d5os5e4uOcTkjLztK6+ks1Spouoon\n" +
                                        "PV0h17G61K19yYXidXcnFwStCXIwF0Jb1VEqiVGJ83q4UoyYxQ7Ylmwsuy++yqN3/xFHH36T1K/J\n" +
                                        "+u4TGq/GaM0CMZqBKNbh6VmdP2J55UDHj9poP/8wp7NljMFWHwjrPAml4uTq0pNLwoiDFOvP6OCC\n" +
                                        "C2rqZ6B9kp0mYZlWU7Z+TpMm8zmy5HSo7YUT9V2PeMDrB5gHclwQcySNIymqL4GxDtsafLD4xmN8\n" +
                                        "w2me8Wtv/yrDZsvSz4jnZ8waSwy3CFkY5g3PUqS0IJue8ydPWe6C3XmTbDLe1Oax3t6SM1S5vLOX\n" +
                                        "bLos2q94UdR3ghfGlNgcnzGcnHH+9FO2zx6QUiKjzijGFEIbOIiQLNcAACAASURBVDi8yermIeeH\n" +
                                        "N7gahWu7Adt5ghXd3RkhSaqdheiKSRyxem95U9mupdC6yTk5aXSwUicuAFkvzy2aJZPTiAaFLxA2\n" +
                                        "+LjHiz/8b/LJr/0d0gfv4oqqxJ33zJcLHZCuvoKddaTck8vA8PATuq6jne+QYqYYRzbKWqWAqwIT\n" +
                                        "U6nT0TiMSzgzAwy2FKKBbMFlg/HKn7dyCLaGaEqkMOoVanTEnA7r1EsaAfIkPdPFOAKXAKGt/Wam\n" +
                                        "SManuIUmUWJm3Pb04ZwsiTT2EKfgapVdi1Mf9GGw/PZ7f6hI4uk5T08eYLwl3DhkNp+z6rfYrBQQ\n" +
                                        "1zWcPTyiKUJz83VKzSw21fvS1Ca6SL4wXb1Q7Nb/r6SLTDJRZVw5szo95eTedzh+8DF5vod78fOE\n" +
                                        "ay8Sdg8Jsx1sgdRvOD56yObxR5wdv0988QYxHZAO5ux1Te2HKppsai9BbVpr9FrOymiwlZNfvMrQ\n" +
                                        "dJDSnZD1gvMQnMWjimlTpLr1ZUrsKXFDsY5weIPP/uxf4cO//1/jjx+wM2/AOmJJyLU73Pjij4EP\n" +
                                        "uO0x6eEHuLJm59pVEhaRVL/utESXC7kbUEWtRRfatkOcU+Yq1U+sTAong/czIophYSKGFhiBsYLO\n" +
                                        "z62oav+o1X0aVJ77ZcDY6YrUq8XPOsPe/oy268gl0/drch4Zt6MaVCA0LtAGR4OwHTMfPXtA/+SM\n" +
                                        "s/fvYWOvurdgsaawmDdsTcKsR3IperILSNpi/YI0DFhxuCxKhKvThQKD5flB4+Jg5VwoKIZkiiDj\n" +
                                        "hvNPPuDZh+9hb71O99kfprt5B7s8wDUtEmYYHE0eGfJIPt9y/vE7rD74Oj6LeuYcWrrm0pRy2ls6\n" +
                                        "cdVYVvduYnU4EKOmJZOniNS31BjBWaeiXadYmKm8LckZUwpxdQbDpipdPIef/366nf+Cj7/2y5x+\n" +
                                        "8E2sd1z7zOc5fO37KO0ueXPG+sNvI6ePWFy5SvYOhqRxwiJ1xVQuXsLpUAn131UL8KadQRouPseL\n" +
                                        "u8sYLI0OGWR0VCkILZSRIpFCuvi6qs6p9cvmWrUubT9USqlMXG28wN+6fXjn+tUle7tz2taS4ort\n" +
                                        "umez6eljBBNYdA2LhcZhrGLk2fE5n/7RH3B09BSyULaKDM9bh+nPmONJsSet14wnJ5RyjrS3MUkr\n" +
                                        "YQbERmwOGLw6utSRlvpGVUgGai/lrKnWPoXtyTlH9z/EvPRFZm/9KFduv8qdV/f4yvVDbu8FZnng\n" +
                                        "7OycB2dbng6Re+vAo2s/xmr/JT74xi+B97TBwrKjtZeGI9kYFdJOf3YBMZaYiyaTZnV4Edfo8CCD\n" +
                                        "LrCd8pN8NSgzSFXyaE3g5CHD+gxXHCUErFhm127z2r/xFzGxJ48D45Dpx3PM8UPO33sX2Ryxf+0G\n" +
                                        "2RokJsVPUlLwsoo3qICst4ZUmXm6AKgDheuQ0pPJij3aGtxuM2JnIIPaHEhdQJeEZKWMC6FiK1BK\n" +
                                        "1sAtk3AOnJvEuTAliFnjmSzfnJE7/trV3buH166wXM4xkthuwNiBXHrEWEI3Y2e5ZL4TcH5EYmZ9\n" +
                                        "eswQew7v3mbYJsr5SN5u2KwjmydPGHJBtgOy7bHDWnHaceDo6BPCoqNIohhfD9Jl83iBBdVVwQWN\n" +
                                        "w1qqYhWS8OT9PyQdXuH2D/0p3vzRN/n83QPuLAIHYnjv23/Ef/ff/03e/Y1/yPnJY7q24bUvfol/\n" +
                                        "8c/8BfiXvsrvzOY8/qN/jG22urjtPI2fDNqUmZVMIdTSX0rGORVD9H2ka+bqykLBVo564zyNczTe\n" +
                                        "4b1TELaacmAM9ugRwx/+NubKLcLeVWhnFHEQBckD/eqU8dlHDMcPyGfHzF3GX71GFoMZiyaNZf35\n" +
                                        "JxoOpbJL666RXE1bmHq/SXThKUntKi9uA+c04LxUK8mK3ZWig5IWqwr8FkMhI3lQ+kwxYCPOq8CD\n" +
                                        "Suu+xAUzVsJdT+mJeUNKk00jWB8wrsWWQtvOmM+XtI0gMmJLJATHy6+9hmktZSikcaT0BYmJ1bMV\n" +
                                        "adyQhkhOhbQd1K9dPLOuQ2xgLI7OGKwt1R9hYkZWeREygSp64UxkOoHt8VPG9QkvffXf4is//mXu\n" +
                                        "vHaFpVOTkE+Oj/lv/sv/nI9+723S0GNzw5gTf/Rbv8WT99/nP/tvX+DzP/UW/9P4mPWDb/CwmXOL\n" +
                                        "iJ03NHbaFSrJrxjqenvyMp2mHnDe4I3DGZBsyFmDyUOwNN7inX7g1gRcuyDIhvT4PeTR+5wWy1gn\n" +
                                        "PsmZnHpMzqoj8J62aynGk3LB5gGyVkpTVOZuK6pvcp2MBWWaZqU3m9poG8BYh7de975SMK623rbD\n" +
                                        "mBk4qZ87uvez08oKiow45WpX3pZVirIxui92XJIgqaa4VI9YU7CnTz9m8/gTtscPGDfH1R/T40KL\n" +
                                        "857GBQQl/fdDIvcr9vYWzPf28Bj1yEoDpmyxeYuJEZcEl8HFgouFuXMcHOywiYpG25JwRcuodWr3\n" +
                                        "M0Wi6VQ13YLV6tAqhZZSGI4/ZHn7Dnff/DLd/g7JQhLDFs+v/vLbfPib/xfrLTw9tTw42vLk2ZoU\n" +
                                        "R9bPHvM///zP89b1hj/zE19mtZmz2n7K+SozRv35KoQK4uoezSgOZ8AYS2haskCwHii4psEFj+s8\n" +
                                        "obV0wdAEi/ce7wrOBUI7Z77Yo5nvYUJD54QdG1nS07nIovPMFy3etwQCiMeLoRGLnxTgSbBpqlhq\n" +
                                        "mOSQyhGrjbuty2RRwzfEkI2v12fFpKa//J5G9toZwXrd+1qHM0oZci7gsOSciTmqlXhJXNhVZTWb\n" +
                                        "u8w6rKYoOgsRs+A3mxNmM4dvBGzBua5+qA5xuU5HmXFM9NuB7XpLLh1PT084PX4MMbM5XWFWPTML\n" +
                                        "s3kLRt1fxAj4wrA9pwV8ymw2I6dPP2H58qvVH04XhhfWOZddO5e+KrqXK9sRtj37P/gjSLdPj2OD\n" +
                                        "EIzBS+TX//dfY2dmMIvbnJ/fxxhDlMSz8w3L2RX+6W/+JtvVU75y9xpf/+Hv58nbv8zJ3RlNv6cc\n" +
                                        "favlXyYbnQu8iourJTivNJm2gaB7ulAsXeuZzVvm85F5Fwjes00F6yFYB76j6bRfTDGqm3JWk49c\n" +
                                        "+WITlCKlkEtBcsXEJFLKqD3VROrDatXKgiTlhk00HirD1VnLmAaKSlUvJmzfzBE3x5qIyBYpmk09\n" +
                                        "NfYmODANMkYFVhE0B9tASUgxhKSyLzwXTynnigwWsPOdxf3QtdimVdKZ9UrsFyGYTGMSxhaSJIYx\n" +
                                        "0q+3bM9OMBIxTaA4T9POdFFcPad0SVoQZ0gWmp0d5leusD1+TB5GtqdnJJzK5ifkezpP0/AxPVsR\n" +
                                        "ICne0q8ps5Y0u8WYPX00bJJhI7Ahc370iLtvvMqf+69+noM3/+V6xXpyKTQeTFyz6lfMneFf+bEv\n" +
                                        "8aTMGFcr+m2kTJ4RBsRBcYrmmFK7eJRJEIxgS8SVBLEHGQm2MG8cs9azmDUsli1t0yhhsPLUrXUY\n" +
                                        "12CbDtfNCd2CbraknS3omjmtbfDG6OBQp0lTEpR0YQ4y9csT3DDJsd1UOSYLy1yvJITYb9TjylTr\n" +
                                        "cWNx7aIyP7Pi7LlQUqxCZatWCs7pbth4nHi1RBCwxWKzqrd176oHiZpVbZPghvG+3d0/uDdb7NA0\n" +
                                        "c0qxjGNmMw7EFGtCVlFAUCCmzJAiu22HiJZ8FxrszGNmhoghFYuIV9vIVPASufbyXUbvWT35lLOP\n" +
                                        "77M9P672hkogM/VDKzUYAKvMimwL2aETilhEVpTugDTAepvYrArrHs5FOJOWF1+7y+rjB7z3y7/I\n" +
                                        "04++UbN0YOlauqZj58ZtDvdfAGN59dBx+MaPsH72hDRsdVmN2gcEZ3BGAzRzVPQ8JaHErBEn/Qa2\n" +
                                        "58h2BcM55C3BCu0ssJzPWMwaZrOgfvYoQ9XVjGlr0L87oyFU1ThterVECmkYsTnhsj5MSqzDjCUV\n" +
                                        "anwJdSqcHKx1+MkieJWkYCmUsWCpDj/oZFiaHWwlHOQUSWlEctLqyIRXTRI6X+eqiVqjV7HafpsL\n" +
                                        "vBE0T6mxluV8555fzDqMDxXHscRUyLFgi6FxlmAzWVQBQhEa41gs9jnJHeM2U8qAFE/oOoZBOe6d\n" +
                                        "txVsjJjQcTYUnnz4u0S7hbLl6YNH7F19n73X37jAVsyFUoR6BSl3vbWws3AsljOa9pCPHjzhwXqN\n" +
                                        "Pz+jPb0KiwBByE3hK//+n+cf/PavcPYrf49rEtnuzZm1lpt7SzCFf/XP/nm2zQwrQsFz527H6UeO\n" +
                                        "3Zlj2QVaXwOhprm9+pWmLPTbLWHItINlNtMFtpERJOrbbTxelAM2D4F5G2ibzJi1AumLUyoGpTwu\n" +
                                        "iRGJUUOTBJ34UqYJHnKq9k2TfXZRyZhoNStTQyNS5f0KKmskcXXoywlrC8lpFTYILrRk44BIlkSR\n" +
                                        "BJU+o5yqXKufxbhW25QilJguDEgu5JkTR04y4PDOcvvGddq2uWAgIyjfOxZRNxhbKTIYShy057Hg\n" +
                                        "whwrjv2dBUeDxzaFlCLNrCONhf50TSNCLqPeu6WwfXCf7WZD2Q7k4QzDTcp2RSyJYJsLD/Eil454\n" +
                                        "1gqdg2vLGYczz9wXZtdvcn3p+NYHv8n7OwuavT3c4jomBOyeMPvyW3zfz/yHvPc//gKfvZ6xboYz\n" +
                                        "mfMhceWtL/Mjf+7f5VzUUfiT3/9dXnzwNb7nrddodnbpOpXj54n5mdSKchTIfcQOI6aDmWsYoxBy\n" +
                                        "weSRnLeUkgizDidB2QMidMHSNp7NAKXmKhoqkRB0wV3U171MPJwsmFzqQ1ag0+SRPA66/ywFU3eB\n" +
                                        "VM95IxpAoLehjv3WqVpaUo8PgVQHIOsMtplT7KxCBYp3aXul2wdrIEuqC3jd2xajwHSZrMJFLc2N\n" +
                                        "VZjCmIIVYX/nkM5bmi7g26BjdapWkLlExGaaxtO1DuMLqSRyTkiJFDEMw4ZkZvTjli50SBvox0zT\n" +
                                        "NUgfOVv1WF8IbYcNaBO46fEF2uAJpuPxxx9x6/XPQrPAloTi4Qmdry2gDXFwWjVithgsV+7c4kd3\n" +
                                        "Ovbf/xrftBts+X5ivMGQPTt7htf+g/+E+Z3Xefdv/w8MR59iZnM+/6f/NX7qL/4l+sUeT05OOPon\n" +
                                        "X6N7/B5vvHidDQ2LbkZxjmSEJI6UtEqNGfptYXO6IpDYsXPKaBhFSYQeS9oUpGxx45Z5u0vA4WzG\n" +
                                        "u5q04aGII2erVptGXxwRT3LgOgdFp6pY0jTrV7JgVReJBpaXWiWMXCLupY78BsfkcgyBYj0lbmjm\n" +
                                        "O/h+yyAjXgql2ceZBdGM2NKoGyGjTuXW1xfgcrOA0fwkYx2mFLIEco7a6xlDKJnsLbeuXCOUxJPH\n" +
                                        "jzHB4tuuuSfZfjUOhTwO5FH3Ud47dSWxhTgmUorEFMkJUk4kO2CzY0wjTdNQdiwpJ1znKdHjCDTz\n" +
                                        "XYzPeOd44cU32dk9ZPfKDZ4dremfvsezTz7l1vdcqThJBUnRg2SKUMbMaKGhYo1JWCPs713lS2/u\n" +
                                        "8tLjj3n4nQ84+eQacf8lzm++RLyxz/4P/CT/+o/+BHbYMg8Gvznj6Qcf0D15yPL0CS90hfbaIeIC\n" +
                                        "e6FVwFCq6WzOFxTcIWaOz04Zzs7YaxriDPq+YBO4nYZ2FkibHtaOYkdmO2dccTMeY/DW423CO4ix\n" +
                                        "Cl8FJBdtsgVcyZRq64iAyRlJU1WoIlBjNLErq/1lLkaDMYuQU1G53AV3XfloxnlyTmpKjMc0LT4p\n" +
                                        "bCVhV+NXqkW3dZ7iGs2SNgFbtBWSEi9qoA6ijaa8SMEUp4LXKCRTWOzu80M//BV+77d+gzt3vpe4\n" +
                                        "3dzz1rp7koScInEQ0jgQjI7wzrU4G+iTaKonvmI5LcFmGjdnsz0jxwHbdXjfwCyTY6RsRsz2lDCb\n" +
                                        "8dpbf5qdm69CiTTGIs2GP/zwXeLZiU43lYVpTXWuE3350pAYTWY0BVeUwDckYbOFvXnHK3d2uFMG\n" +
                                        "xu3I2dk3OP3Gb9O/aygmgK+cdm/Yn3XsLxoW8xZ7ZUmKmeDVTE6Mp9TYXifQpwHrNXptiJnjk3NC\n" +
                                        "FKL3bHowZWRwmWG9wrkC4wBxoCXijeVwp+UgGD61CW9r/qG+3Hin1kWpFH3rc8TmKrDNGVsKfb9R\n" +
                                        "a0ajiRRObD2ME0NU41pKTDUkQHungl5dehUGVfE4Sxo0Qctj1aa7O6CYgiPo7ec8vplpZSyWUkYm\n" +
                                        "o1tyvKiMoIm0tmhPZjAqtsBwuL9HJnPr5Tu0vqFfndzzpdh3KEmZCjniSsGFisaWTCwjsYwUyTg/\n" +
                                        "p1nuknKLHRL7y8hAj6xG8vkavI60XduyiUIcVtx5/Ytcf+NfQMqAyQOSMzvFsti/TX92zhAj3Wym\n" +
                                        "b0hRfpAmuieGlAmjoTWGWTBIsMpqdYYxCt5GgncsDw44vH5AMBkr9UowGWccQVTUVfLAMEYkw8w2\n" +
                                        "YDzKC7P44ugruDjrWoYEEEmbSNwK1jvWUZDVlpXJzF2hLYnWigp0ZUtHJm0919KaRZijwVIq5DR1\n" +
                                        "0Z6LLnNthjwolCAVWS8xUbIQmhlQzd6qT6p6S6jVEkbVjNM6SfEvQ3JycT1JCBgjND4gKVEo+CCk\n" +
                                        "2U3E7aHEMt3PGgzGtrp5NKOeA9BwqunAim4dLBZsjTeWrM28abAp8t633udz3/cmh4d7nB0dvO1H\n" +
                                        "Ge+VYirXG12oOnPhfTWOkbEfSbFgg3qQOruk5IGFn7HTLTmXRIiZtB0ZRflbPnj66Ln5uR/BuFYp\n" +
                                        "w6bBuAEX5ty4dYfHn3ybm08/ZP7S56r0fXLDq5nKRYgII5m+gCs63mSTq1DUqyo4Kw0Yp6YVbmp0\n" +
                                        "iXWtZiAlnOiaI4tgx6QeouiDS00LRflKKQ+IFE7OzxnqZDjGgVUWLImGSEsmmJG43fDw6AiThcO9\n" +
                                        "Jd935xC/p7nLzk1EpjrrXoCt1KWvToFUUxLv1Nv+MgGjRq88p3uUCvRdVhKFZtQzVCjBgesILuPa\n" +
                                        "DmyP5AY7u4rMXyXaVtXUwnNWALorLROKfsGTq7jaxazuMDYgNmm1MtB42N9ZsNxbsr+7R3ANod09\n" +
                                        "8b/wc7/4zs/+R/+OGsyWikfUVAkKpJgZ+lH3geOW4NZ4P8MYR0fLvt1DWuFcTjBdoAuO2I+IKzSl\n" +
                                        "Y7l/nZRHShSs9xQrFJO5/vKLbFfHHH30AVde/Lw2h1LwpegAkQtZDEPMeJM1KFw0f92XQlOEmDUd\n" +
                                        "VFwBZ8lWJd8ZtZm0plTnmbo/ywXjNCggIxinTsuYwpgz1jRKdEsFGQvbbWTIiZwFl6BsN+TcK41a\n" +
                                        "CvOSMann0cmK6zeu0ezss7YN3jWTGVld2tV2eGKtOKsYVrr81xYuZPoXjsZGW4QpEGqKlbuw5Z4O\n" +
                                        "Xx17WmOxoUPcDt6eYv2S5D0hHND72/TmACZKD5dIfammbRS1EDfW6UuaLtF4qDvDetVmEsYk9g92\n" +
                                        "uf3iTZY7O5QxgQ383H/6H7/jAfqz8f44mjsxoii7n7jm6OKxQB8T0vfgBspsi3MN+IZ2GNmXBTIr\n" +
                                        "bOSMIoluHkhhR91qjGfcbtk++hSJW3av3cDu7tHuX+X6S5/h5KPfZxw3hHmn/gWTkLTupXIp9EPd\n" +
                                        "PxblS9nsKrfcgMsUZ4neqLyreiaEihmRi1r8WOV74zRYGxf0nXQWTIvPhpgG9biSyCZGRtRgLMfI\n" +
                                        "+uiI7fkZTdfQzGZEcUhKfPbmDp/7nhv4RcOsDYT5glVxuGFQ0QXTlWJrTExlKAiq1rZVvpWpesaE\n" +
                                        "RigUSLGeA1PHeo2gU1Oguv8rWgW9GAgN1rXk4NhKi2szYu5wbm8QS409Qa9cDHVlpN9dkVS9Kwwa\n" +
                                        "wQLF5Ms9IyhkUdsHYxW5v3ptj83Q88Ltl5BcePz44X2ogtWz9XAvjXIni+AcWJsJVmXeypc2GAkY\n" +
                                        "wsUesRjHWAp9jsjQs9mcEOZzhjhirE6VZrHLdvuE3M8o21OaFIlHD+lKxu5fwS4OiAmG00e4xWcw\n" +
                                        "ViuaMVU6npNeh6bQZ/V/8kUIjcEVo5iPFbap14fqamwIhVg3WKYfyKtzmvkM5jNsXe6WKrhUo7VM\n" +
                                        "wYIzZNFk+H5I9MkiQXendjZn7huVNzUBL4bddkGz39DszmjngdliTraWvNU3f6IRTr6dtujkWUTp\n" +
                                        "u4qEm4uKKum56gTaK1bJWxG5EGfwnIBBPeltxR1bcjMn2j3Ww6LaKjmlHRtRv4f8/DVap7+sq7gi\n" +
                                        "ak1grK9T5qWFpO4gqzIHS3YNh7ue/Z2OvZ099nb3eProIcvl/N7Fwer74Z045q8mEcWNkoE80s40\n" +
                                        "prCgqwBrW8QqSpyHzDhE0hA53ZxidxcYv8TEgbhdYxvVrq1PP6K1r+AwBOf0DTk9R4D5zlV2b77C\n" +
                                        "5vQJi2svYbxHrChXSEal9qIct5gyQ4YglpATJoAElWQFEQqJxhgCBWuFhNrstCkjfWJgSxMCuRSs\n" +
                                        "6IpIHY/1+qTxZCOYULcPRfP/QtOp8sQYUlTUu2sst68suDZ37C003DzMPbYJjAW9jkvVKVbAd1qw\n" +
                                        "W/Twakds1Iui5EoFVg0kWHJKYAxF9CoWgZInot/zfZtFTM16blpsd4WUA1ISxYIpERMNYhJSk8GM\n" +
                                        "THLToqBn0mTXUmlAVpQ+zsWfIxf/q2dVRSRXDmZ4m9jd2eHs5IjtZks/5HcuD9Y2vxPHniIj4tR4\n" +
                                        "QnIkScE2KrQwYhEP2QZiHkmDIfaRmDObuObGzmdYJ4eZ7bGNCWGgZMfRk4948cYrJAemeCQ5Nukc\n" +
                                        "+3RguVxy+ws/zMNv/gaL40d0118CRqw1FKM4DkUJbDkbxmLYSsEFvaJzBvGGzjntEUnkPOKdmok5\n" +
                                        "Yxmtxe/v4IZI3A7YNuhwMBSKs7jg1dS/z9B6DQOw+vu99fQmKRwymzFvMruN5c71XRamEEiELuCk\n" +
                                        "EHwDRqkmqegWI1tbTTe4rDAVJTeTmljHb+1zrNc0kHrNUaVcOSclBkI1ztWezFQYX41xDeKX5GZO\n" +
                                        "f3quFaeCrSo4tpiiL5Tyr0zdeFzaORVUXJILWill+v70b7ZOkdl0tE2ibSLDVvtTtpG9q1d48c7t\n" +
                                        "ty8O1ubk6O0so4oDgkWsw/k5KQYsljhMZvcKWhYzkseClERGvR2a1hMObrI69pQyMvQRY1qerT7h\n" +
                                        "+tUVpliSF2yOSCkYX5BxgzPgFzfo1zqy24nxYD1iEhBrIphjVN26ItFFNYVShOQtTbZISmyPj5kt\n" +
                                        "GsJ8hjMW7yqgux3IIxgzV/3eZotvG/KOuqsoSKoNcsyFLniu7c843Yw407AIlt3g2e08IYAdNvgM\n" +
                                        "JrT6MOpbnLHEUkgimv8sRg8ZVKtsU9uJ6mKTpwVwujxolU0hFaxUTr72Z9YYbNGFcMFjLWTnET/X\n" +
                                        "l3rsMTLoAZbJYA6FL0yu8Sv6jNXtUAXKZNVkanv+XLECnUJE9Hu2DucynpHYZ8LuVTarE6JpuXH3\n" +
                                        "Jb73M997ebDeeeede9/3xdfuO8sdjcK1CrYZBcFKgpyjGknQYySQopbPIW1JOTHGc+azu+zZaxoh\n" +
                                        "EqGXUwqGs81jds0eyQ50OddS2jD2kWZYcf3F2+TNCWYseBeIJutW3XpK0r2YlayrHbEoP6lQKnVD\n" +
                                        "RP2eDIJvW9YFmlFoyAxlpKleolEE1/fYJuC8JxahbAZlQzohZ4MJQQ8JcHWvZWcZMEDrnAYauApE\n" +
                                        "lgbpDKX1F012NoY+JcYCY2V3ZjHauBs1KikVKiiogsdIVvZCqeKFWqWmybCImqZg9Od3OnIo78ko\n" +
                                        "9cgaB82M3ATGfk2RiNQVmAYzgZFCSbZGHGuFrDpotTvgUpwxGYTIdP3Vv9RiQPWYi3nLnVeu4a3D\n" +
                                        "WWHcbDl6+OTrf+pnXvlu1+S2Wb6NTT8TgtXq0+iDTWIYU9JNfHFIo1B/SRBzph/WlJLo+zVLJ5jg\n" +
                                        "aOdL0mpGlhEpmW18wt7sEDNo6KUHsoyUYUvqV7jlISbskVZncHADi0OMQ5zXHzqpUsZYHa23YzW6\n" +
                                        "NsoPSq7QmkweehpjmM07+uo/YfqI6TwSrK5Q+h5SwphWeU+uwDzrn9cGPJDEMeJIxVISFAYEaJxe\n" +
                                        "HWMW7e2cwycVeI7V8W8sMMZMSvniUAmx2nE/x+LM5eIwUTISI6BeU5ONUCrVVcwYrPFoemNRrr4x\n" +
                                        "iPXabLczWFxhFIF8qrQlBJGASMCgvLA8QS/VrijHhL0kvulNMdHAa+2arJ8ElGePher/f/XaHmcn\n" +
                                        "I6dPj+jmu3z0/ntvT+fpOTvu/LbJ/ExxciElQtSjKadCiRpiY+oFn23VNpSENRDPVnoPVxqHjRnp\n" +
                                        "e9zOjNP+Kdd3XofsdM8mArFgysD6yQP2ugXOt/TryLJdI8Gr0WtdRpc6FitnX7DOKpVFEjGNzIKj\n" +
                                        "BD8xhZCUcQZiTuqEJx5XLDZnvUZKxptB93JkJBd8O6MYp1EhjWUwmT4XtjFVMDPTOKF1QosqZooI\n" +
                                        "ySopMZFIJdMPmW1MjBlNNJswIzN5TMlFF2xtzc02uuubfBdUFaMBodjq7lLUZc9ZPaDqnmdwPkB7\n" +
                                        "jdw2rFefVB+LhJSASLwEY01Nw0An0UlCX5JihNShYaqaUzrH9GsCdycwaBxWPH32lDYc0nQN280p\n" +
                                        "myH98YO1OY9vYyJNsoi0iIn4rqafZkipYGzGBsE5S3CuvpFgaBnOzvFd0QHDqIe52/bqD3Vzj6eb\n" +
                                        "e9yYf4Fczi4Eq9kIbDPnjz5h7/ZriG+JZ8/wh9fJE6XDamkqlS4sohsJK9VTHUMfCyWPJGuZW8Mw\n" +
                                        "bmkbhy8JZy3ZGAJg4oDJI953ZIlIrleQEbIL+lASjFboJdPHwnrUZtxYS3DQGmFGgXFbVy2F2cxh\n" +
                                        "goYgbJOwGQpjNkRRXnhB4YLnCsHFQzKV6GhdUMMTqF/XYl1DrAAmovEmAFh1ATIuYLtd2Nnh6foR\n" +
                                        "Ja0oaY4a0jUU0dRZbK9CipomIaaBrP6jk3PfJemmDgj1Rf5uGf3kDKQuhnt7Bxzu32K1fsR3/vCf\n" +
                                        "4Wbujx+sb3/72/e+93Pfe78U7gxDwjeWIF6T4a0hkyFnmqLOwDir36wF6wv9VqXa0hns3GJnii6n\n" +
                                        "8w2ubTmSj9nffYk2zMnjqcbJimZNp7NnpOUezcELDE+r8mpPTgAAIABJREFUuNNpCTdyYQnGxNaX\n" +
                                        "uoJK6Aoq1uqRjSBBHU/Gbc8MQ6ipW9lBjpm592gaiFT1j9JQojEKRpLYbCPbImwznG1HNXJLGl83\n" +
                                        "rjesz87oxw3bsCAAvgzc2pvxwq3rGGfYZGFIRpkgUb+3qSMWgVJHxVKmy8Zr6KRESLlOhwZ8wRUV\n" +
                                        "mkgRxAlFLMYGvEFlZPNdTocTxu0Wkxv9OgS1RZAGYcS6HmMLroK0GpnsdGjQEsUkkc8Vyb8khtbd\n" +
                                        "awXbio4MSBrYDgPnZ2eM2y3Lxd7X/87f/Bsnf+xgAcz3b/ySNfEvkwfEaEzuFGiUipqblqwCAauk\n" +
                                        "BO2HvCVpSF7FfASs7h7JwubBA9zqgMfhm9y++kO4soSy0u+16KS4eniPncUCv7zC5vyExe4+eeIG\n" +
                                        "VSCT8hx3u17VkgvkzLZkclUrN8HTiAPRnWKKCS9CExqGkrFZsS5N/tI9mzrxZXKakrlU0l5y4uT4\n" +
                                        "iBOzJO68gixa+iunulPduU2/WnF875/yrfe+Sffuu7x+9YCbb7xJCjMN7TROVUYpV2hILq7DKVzz\n" +
                                        "+evGWFttQjTJzJSCyRp7Ui7MOAwmdMhin97A9vwMYiHX68raKnSQCEY5XUWiIvhJkOyruqdaFVW7\n" +
                                        "SYzB4CqWobtBELUymqomAAPbzRnv/t7vcf3gJUzIkMovPX+Wvutghab761j/l8voKv99xJCJVTXi\n" +
                                        "nF4JzjomlqP3Dhd2uHLns4gLlCEjUZA4fVW9x/uPPmHWNZztPmZv8RJJIs24pdS+Q9ZrVh+/x+Er\n" +
                                        "bxH7Ff1mRQjtBfHs+aayFLVqlKIp8g5D3EaliRg0TYxCFGhFGZGBTLEel9XLypZq2l+nKlUQF8YY\n" +
                                        "se0cbw2tLXA28PFHn7C6cgdm5+TzT+k//YD+O++y3fYMJ6cM63PydoUpAw+aljdj5JU3f4CYLVms\n" +
                                        "SvS10arX4XOI9rQXvDxddc2jD1eR+cqSwKvkyxukO6CEJWfH9ynSK0hKCzS6uUDJNMoWqUtnUUBY\n" +
                                        "D4hexga1OrIu1EzHqZdVivPF4Z+wNQRntQLGDClF9g8OWDSLv/XPPVhfe/tX7r311Z/8eh7LF0lb\n" +
                                        "XNDY1pJGEKPCCddhbKsfVs40sxkHd18kHFxljANkj/W1o0VtnKkPzQ4DRw8/ZPbKHvPZHiIFO+oG\n" +
                                        "Hwr56Jhh+QnN/hXWR09oFoZRinLDRDEiChRzORo7qwICaTxDSfiY0PZVfcmThUimi0JKA96CspWS\n" +
                                        "LrWbBldU8CBZah+iFkazpqEZzvmDX/n79JsN0dYcv0rUNBVIdRZaY8AbkMzxw4fc/gIUW3eCVUU6\n" +
                                        "WXbrrauHKVsqpGAp+MonF4qJUFmbxlZXRXHYYMB3SJjxaPOJ9qzGYJ1HjMPaDmyn1Z180StJNtXX\n" +
                                        "oX4fokt3Meotap1BXGAyA7bV+lHxLy76MIrDmZ4xJ+7cfJ07d17CW/P1v/FX/8q9f+7BAhjPV38r\n" +
                                        "x/Gv5Tjim0Db6QcuxWJosLYF0yJmRIrg3VI5UJXD7mi0f7BW6cxjwiQdm8exJ5TIw9//de5+4Sex\n" +
                                        "i32K20IcFAcC+qGnQ3DzGWfn58xnHTHrk5SKUpupionUzD00+rdu/21U6kkyQrDqZJdEaEZogyV7\n" +
                                        "XaX4KjWXonY/upDWK2TmW9rWsJkHchlx3hJ8YGe55OrOkmt7O+zvLjiYL5kv5rguYEJL8h157wrn\n" +
                                        "jSdtk/LGyzRL1Ro+VV4mY7O6G6xNfTG1lc6Vh1UBY2tV0VyM5cknv0fpFL/SqDpbTUcUl9JYO4e1\n" +
                                        "c0QEK5kqq1ahabWAlGqIUlIGGbGuwTaKfZVSt52VRaGczMw4bOmCXrFShKZpv6ta/YkHa705/aUg\n" +
                                        "9q/pnVqUvlItI6tlgPYAFYyV0fDsow+Jwzm+Ui4Wh1fImzVpGMlJEWQjwursjJ2XA6fna+5/4//g\n" +
                                        "1bd+GtwMOzhkcEoRMSCxYG3AtYHtODJrGnLuSTmprZLRidBiNN0qJRBlMMSSsWPEiRANJGeJJZEo\n" +
                                        "jEYoJuCsJziHGJWpKbVbRZ/GGpwNFBtwFN548QV+8gd/gGtXb3Dt4Ar78wXdrKWbz2gWHTQOaR2p\n" +
                                        "ZM6HyElfOE5C3mRKUT5YzvnyOpkOF1yEmldfDnW0KZcnUB9u0QNjoMhAjoXVyaf0p98B7mDaBisN\n" +
                                        "iL84fMXUZhsLxkOxOqmLwitGRl33TLQcKeqMQzVISarHJGdMVfBMjodCffGahrHvySIcrdMfO1ju\n" +
                                        "//0PTp4+Pbl25fAtF+wbtmkR0zIWQ8y5ppkraS+nQpERipCKsNkesT06p9+ueXr/Y9YPH5FW6yqe\n" +
                                        "pDIVCsvr10nDwOrkCSIjy1tv4LxFglcZWjuDWYuGnYsi/mgfZJlGYa1aBWWalpQpsSLJYpQBYLKq\n" +
                                        "R4xu/4tksmgaBb7BuEAxKkYooJiZs8pcazSFQ4IjL3e58vrnaG+9jL/+AvHggH5nh3UbWPnAxhl6\n" +
                                        "MaySsEnC2TCyHTLDKPQJZYeUOmRIQnK1JSjVF6wmnk/Dvqmr6jypoa2GNhUp5GHL8dlT+u0jJI5I\n" +
                                        "aTDLQ1JyCA3OBpwLtR/N9VrztRJ7jA36yEtF5K0OFpM//QSxi9QcxSqKFSyYBpzHmMKyPaf1jrPV\n" +
                                        "lv39xf/6t3/h5/6/DxbAtcPDznr/0y40urMTEMkaZG09BbUdzJUEF8tAszQQWsgRHzzJOtbrHjOO\n" +
                                        "NKbKs0WYHeyTnYFhYP3oQxbdnPm1zyAh4EIHTVAgsQKClEJOIz40GNMQaraeLmMNFLXhdlWm76a9\n" +
                                        "Vq4HahIMiFFU21nEeUXEp4NV7YeLMbozdA5MoBinFWibOBszqzHRZ5XHCYLVaYZiDDEV+jGzSbAa\n" +
                                        "EushEidztKICCl3VKNOg1OXwhT9F7cUuaSoTM1Sn0357xPGT+6z6c5XGp0gsBrd8CSkzRBoMoYaN\n" +
                                        "+9rAW4zzirwbHbpsZR8ajFKA7HQE6nrMmAuATbWe6OBgLcYKnR9YNOcgwnyxZLPd/NUP3/vWO/+/\n" +
                                        "DtbTo6N3rl69+pesb/YxriYt6J1d6tufUialREqJ2D8j5WO65ZISlPzvgDCfkXDEoeDRBtkvO0xo\n" +
                                        "sNYwbAeO7v0B7d4Be9deU32d13Gb6oeZciIOZ+S4JrQdxjaEKoSUKjHXNmuK4LBVHmUQ5xRldl4r\n" +
                                        "lHNQQ7ezXIZ2SxFldFiDeF3QFhxDsWxioo8DMauHgXeK6wmq3ysoTXwYE9sxsx2F9Zjos8rXRaad\n" +
                                        "m1QeVqXCVDxQrxa5yFuuML2+BNZRZGToj3j44D36zTGu66qxii66/c5nwe4S7Eyrk9Fqa014jlbh\n" +
                                        "1D4AUx+5rXywiXpRq2X1p8BNWY1cHixjgcQsbJi3a3KGm7dv3f8//5df/Nk/6Qz9iQcL4NqVK2DD\n" +
                                        "T4nVyNuSdDnpTJVqS1SJdonkdEK/2hCCuq442yJW6b/togUHY4ykXJjPOkKjH1p2ju3pGaf3/4j5\n" +
                                        "bsP84DW1DchqspZTJG7WcLZmdfSA1dOPmR9cQ3yjUiamdUkdhYtiMhorZzDe4YImw+IdU4RE9bRB\n" +
                                        "PTdq9rS1lJrwXqxTuwCxbMfIkDO5VBC2Lmet6NIbUQ3iEDNDzKzGSJ8zMU6JYkX5VEWrfMpRaTs5\n" +
                                        "M0VDlNoYT197NEI2hZJ71mcPufed36ePveo0jaWUjBG1gWx3vgcX9pU4YCwWj/Etxqqp3eTNpPiW\n" +
                                        "gwlCkNowT4erZjCaCnfYyagF9XP9f8o7lxjLrus8f/txzn3Vsx9kkyKlFqkHHdmwiACBDGTQmtmj\n" +
                                        "MDN7Rs3smTLLVLMokyQzTwJwaCBBIiMTTYy0HAMx4sChZSUWbIvqFimy2F1ddW/dxzlnP9bOYO1z\n" +
                                        "m1YkkZT4snOABsgmu/rWvav2Xutf/0OdCgMuvknoAyc3rvPFX/u1f/Fnf/xH/89p9XML6+Dw8Pu2\n" +
                                        "cb8nME0xk1NlNprqd1LU8DURCGVLHhLxasXxk08RXIsz6ikqOegxOp1ipzPcRKVGknXvFYcB+sjF\n" +
                                        "j36ASY/w02OMdcSQCbsNw/aKbn3B5cU9Hr7xV4TtBdODA1x7gDGKp5W6WCqVq8TobWD09Bl3iKZe\n" +
                                        "x2C0vwJSERK1oPBk40jWE8QwZFFleMrK/RpPwvpLpBCTEGphdSEzpERSFopCI1k0GzBLTa2oA9H4\n" +
                                        "WgWM6OvL1ZmvCJRhy/mD13j9/t/QDT3ZGnw7rVmJarmUDTSHX8BNjvX7qy4zWFc1g1Vqb+qVVxkj\n" +
                                        "I8mqVLebPSe/LtKxj5F3UyyjDXljtxAe0MyPeP7zz69Oj5vf/dO7d/v3VVjL5bK/fuNkmoQ7OVGN\n" +
                                        "68FYR7G2atAAF8nsSDGTtjue+vznmJzcJHUdpUS882gKqOXmM5/FzY4I257NW2/iQ4+LhSSJmBP9\n" +
                                        "es3F2Z/z8AffZXn+A8yhITY9Xe7YDmsMkatHP2Z9/jptM2G2OAY3VT54HaVSeczPxtQewyq9RHSC\n" +
                                        "19dv9L/rB+oQ5xHryNYTapxKzBBF+6eQhCzq05WzEvZSFlIUQip0MTHESMpGPedHO6KsoKTajqeK\n" +
                                        "J4zIe3UNNNpPiejYP2ze5vXXvsvFozNt2mv+T0qObA9JZVJtGRN2fhvTHlCMyu8h7537nKvNOiMN\n" +
                                        "ytUTStmqIxt1jCh+Z0KaGbn4BXW6MYaD2YB0D5nMDrl57eSb//7f/Otv/6z6+ZmFBXC4OHwVw+9l\n" +
                                        "yVPJiv6qOZTu8QSLbQYyPWnokT5zfO2Q4+e/gORC6HvIHcY5Tk5v0Jw8yaQY4mrJZn3FZttpBp6o\n" +
                                        "INY3hWwhbbfYA0f7xAGlBjZK2BH7SLdcI7uBzeacMqyYTCeUdoG4Zh8lp94DQqk5xxjGvEfdfVWH\n" +
                                        "l2LsY+cWpzk1CSUUJgwpq39DypCz0ZNGRPOFciFFIYoQQibFTIhZCzCqx3vO6t2JKP9J9q7FlR1b\n" +
                                        "XZsR0Yjkfsvlg/vc/9F36cOWGFVF7qxTsUVJ2hvaOcE0RDOn5KZO0OgPivF7FogGg1u9kkd69B4g\n" +
                                        "TXudowpdtbUYhUXFQq6TtcNjrDBvr+ivVmTi6vqTR7/9/Vdf/amn1bsW1nK57I+OT6dJuCNS0bs6\n" +
                                        "CVnrVEHcBIoZSL36ky6OZhx+6ll1D+wzZdiRDcwmE06eeJq07Um7DSkOSBFiJZ3lmAm7QDubY9s5\n" +
                                        "J08/jZ9NkJixzYRCIfUdqd8Ru47GWTbrc9YXbyLDinYywTYHtY+wFc3W3kVgn7Aw/tITzO6vzLFZ\n" +
                                        "NlY55OMGQ9mrI499jBgupCzEnIhpLCi1IJCoBSVF102P1zY6xovUibWuo3LOhGHg6uqCtx+8yRs/\n" +
                                        "/pECslWCF3PWg8ZS2Q9qhleYkZkQZSD0S7rtOXFYkmOHxeH9BKmnVUE/tz1txoxvxDs4+ei/17fj\n" +
                                        "Md2m1L/c7pi7JakfmB0uvvnH/+U//8zTCn4KQPqTT5D0Sin+6yJyXABJCWsSDo/y3izZOH0TrCWH\n" +
                                        "QB46Sh5ovKHrE94UBrPGScYU9CewsfjYYNspMScSO9gENlc9p7cOlfOVE6nvWTQLzflpZ5h2hpQd\n" +
                                        "eYj4aUO3vSJs/4rVW/c5vvUZjj/1RVxzirMtIsNjNYyUfaCRqxeJKnTVLRmj06VSVFIdsSsyjdpF\n" +
                                        "6pL/MZecwjt6J+1DTcnaU9UBYQQX9YCqW4wspJjIKbDdLFlv1mxDJOFoD26yWZ9j/ID1AWcMKWV8\n" +
                                        "0+CycuMwO/xkXh0ATeW+G6Tvaoj5kmm4ycHxs2An1VBOezhsVevYMfs6YnMBZH+qUbS30i2ObiOs\n" +
                                        "y3iEkln5tn3l3erm555YAJvlcrk4Ppki3NF8Y3CmwTirk9k0k22gZPCtp8wO8WED3pH6NeHRI8Kj\n" +
                                        "NcZ7jk+fIIun75eEsMVMGnLOGko+mVAMeOdp2wZ31GCKkLcBm3rKfIEJA6kb6HcdPidc66oEPJNS\n" +
                                        "z3Z9TmoaogQg1pOprjdyBSr3S19DpvLCqymrNvilRu+q2FUqWKhXmlKJR/l7yYquiyQl7OWkWwAp\n" +
                                        "qpGUQpRYTyc1OMt9IHVbdttLzi/PWfYDfdElsPUtvp2QhkKOqhjaMzsKCm+UmkpRKsBpWhX7quS1\n" +
                                        "DiWRFDbkcEXrraqexpiZYuuJNF6PVMfoPToKVJr72I9ZT8sVpDVN03zzL//7H33rZ5TLey8sgO3q\n" +
                                        "8u7s4ORrReREF55WUySKp/ikhMCUMZVQNzw402yYbcL3Hmun6i/VWNzBCVdX5xiTdF83aSs3POMb\n" +
                                        "T8EQ+i0lB9qi6RLd8pKDG0+SssCwJQ4dMiSmTYNTlFP3fQilbYit5eGDe+zOf0TerXAYnPN6OlVN\n" +
                                        "X4qBMTBbTfTr1ZdHEcTI5KwScZHqcpz3C/icdMqTJOSUyfL493KqQU1RCGEgDRvCZs3V+hEXyxWr\n" +
                                        "XUfAgmtxrtlrDIzTSbfrehXetvU0EU2XMBgk1SBO73ThbGoDuV94q89Clshm94hhuKSUoJHGxu+B\n" +
                                        "z1KBblXAPmYwaJqFJpsVa7Fk2nKOK8P9v/pff/LSe6mZd70Kx8cUebkU/quadzS6IpFCSYaJa/CT\n" +
                                        "QMiB9eUa33Vcf6rB2jn+hmNzfkbJhfXDRxzPDhSorHNAKkKzaPDB0G97csm0rmX1cMfwcMP0aIoJ\n" +
                                        "A8P5Q5gdYH1D007oSk+OgveNgqnGYbzHlUTjIDvH5fnbPHzjNUrIHJ6ecONTtzk4fQLTHoDxlBSV\n" +
                                        "qZorQGi9hmSOCHTdnvEOQuA+CkTS4967JDX2KAmpxZeT2gSk0BM2j1hePkRmC9bDgIhRlgjj8rv2\n" +
                                        "MQCStFUAQrQ0TYPzSafKUqqvqSAyYKLBcKDZg+NpUwpGqm2wUbuE3K3ZpciuP+fw6Fmm0+sY9Z/R\n" +
                                        "W6eGEuw9TIsS+oyLuvCXQEwbsnQvv9d6eU8nFsBus7zXLE5eNMW+4Iy6wYlTFYprDc3E1kVm4Nr1\n" +
                                        "T3PrqU+zOL5ByANTu8AtjinrNbYE7LUbyGBAAio+V3DPZGUmDP1QnRoN3aZXx5QwcHBywpAKEgJ5\n" +
                                        "6CkpMmlbRc1N4fD4GCj4xpEbg/SR0O2Iux2bqwveevhjLlZvs7p4ndRdqU1PGMhJiyRL2FtPlxQ0\n" +
                                        "DztFYtJs7BQjefznFEgSSTlQQiAPA6HbEoctoduRNmvCekl/dcHyjR+yuXybfHBKaRpVLdtWF+1e\n" +
                                        "qSrWOpxTt52cM92mAyCTaadOJ8dSKnwzMmkB53B2tmdGjHFzVI9QWydfIWG9q9bi6uOvPWWi5FQl\n" +
                                        "9jrpmJpr5JwCzqQtDA/+8Ed/891/9V7r5T2fWADW8nVTzJ1Y5NiLYHPBZIfLU5Jf4WYTnLW88KUX\n" +
                                        "6Qc1MGv6hL95gru8oJ82NJND3JBpj4+42hRyt0K1g3Wtb6GdtMSoy2WMY0iWfL5kcnyGm88ptmAb\n" +
                                        "xxAGSoZhu6M9mbHpdtgidLsd02eeZZhOFcfyhsyE4AxOArvLLWdvvYV0kaZpmE8XTOYLDm/cZHp4\n" +
                                        "HT9Z0LhKgDOPo3vVdCzqwrpYSg56QklVfGaVrJeiv2fQqcs1U1zSq834CXnQa99YZVjoak6FFVgV\n" +
                                        "0BYHORkktSQJynGrZiDWaYZjkUyRjhg7TNMoGFrUmc8YnWCNjOqbev5aSHKFjRrUBIq2a++ZoBhs\n" +
                                        "EZzbMjla0kwTYbNeST74+vuplfd8YgH0m+Vyenxzapy945zXydA0hBhZnMyQFJnNPDeu3cK3M4iZ\n" +
                                        "3Bh8OyOdn9M8cQ1jF8Q33yTkHfOjExK6D8wiyBCrw0kNVKxGHimLKmB2GyQMisXETIyJsNmRu55h\n" +
                                        "tWa4WLG7vKI7v6Q5WmBmc1Lf65SKKpP3Ik0xpCExDAO79YrNekW0nsFN2WZhlwLroWPVd1xtr7ja\n" +
                                        "rFhdnLO9eEuHDTdXG2uxlGIZLX5MFTzkAiWrbC5JIaYemc6ws+Pq31mZBzXvcJTx56wnctdtKAWs\n" +
                                        "8xgKbQuU8jj4itpn54KIf8c+11SMLFc6sp4IBl1vMan+pKI3ha5lk/ZakqoxC2ALpzcDB8cbUu+/\n" +
                                        "+bf/88/etWH/hQsLoF8/ujs7ffKrzrvb1ljAKSpvGpqZYRc6uvUlB9MjiIWD00MkGh6d/YCnv/Bl\n" +
                                        "uu6K3YM3KRePkByZzOeYZkaOUJK62tmsq6N9zrPXsTr0kTQM7K42xEGJhjYJ3ihjNSU1sjDOEofA\n" +
                                        "/Po1pAyknQYVmXZCyoFJMyOkWHduChq64mgPDzGLU6x34FrEtYi6QdDlRDENksCnJb45RJyS8JQp\n" +
                                        "oO9PqdCD+t1HJEdSSpQhkJoZbr6oBDpNAqOuhgrVDywGUhwIux2l6H7SGUczU9oPWWiMq6qfkdai\n" +
                                        "SiA7gr2jzK0UsoxpXdQiErBQclSIu8mYqAU9MkuNzZTi2F5O2F5OvvPa//iTl99vnbzvwgKYni6+\n" +
                                        "RWl+z2KmpeI5OUIzaXBTQwg7Uu7ZrpbM5i279YaHD1/n5o1bXLz1gDdev0fKAYaeYbfFe8d0PqNg\n" +
                                        "SH0C1IV3HIOLNThvaWfVBxO9eaLKXHRy85bgErE1DI0l5MzkYKoOLENHoMXPpsQh0k4bsugkJDlV\n" +
                                        "ajQ00xnN4hjftDjf1t2arZ4FurR2pqV0KyhbneIsmmWjYJl+TVHYQWIgx0gfAjlEZHGAmR8ycsf3\n" +
                                        "UcCVrWGMoeRECgOh76ufg2YcuSZjbKVFl0qpohqM4LD4vzNkjB76Wap5W33PrK3rrxwhJqaLt5jM\n" +
                                        "ZgR3Ta/uFEf+AwWz6nbtV/rl938mwv6BFla/XPaTg2t/ajEvi5jKMVCO9Px4injY9js2mzUXl+es\n" +
                                        "lo/oTcd6fcHF+pLcQPaerWS6Xc/2ck23WdO4tuJzajVd6it0zqoT77TFto0mJjjFsGLRyJBopOr4\n" +
                                        "ikq5UN6en0wZimewz+NniTxcYX3FZ3JWkiBqpmsaT3t4inMN1mr4unGaI22Muh7jW+gEm1d6pRhb\n" +
                                        "xQ+KjVEUZtDdYCTHRBxttA8OcAeH+r3ltDflMFhMSfp6ciJ2HaHbad9pVAnlvHqajswM43RvuZfE\n" +
                                        "yyghk71WsFSeGUVdDvehmLZBkkbVtX7FyXXhcntEO2kxzu+p4GL8b1387X/6/i9SI79QYQH0V+f3\n" +
                                        "Zoc3DXCnGFMJgAYz7ZlNJ2QM1qtTVcgaURdLxLiCb1ua2YTJfEYzm9LM1Hcqhp6ce2yrDSxWozWk\n" +
                                        "CNZ7jNOELNc4cEoDsY1DbKlmZFYdl1PUpjoXZH5M17xAsII3A1Z2QFH7oFRpt0kQU3DF0hwcKY/c\n" +
                                        "e2wzxbZtXdRqMZMzfnpM3j7AS0Gsx5CxmXeg7IlYMa0isTb0iXx0ilsc6S4xKyRhRpVzPT2Jke12\n" +
                                        "zRAHlNTaqEAXoWmjDg2U6oCoU1zKSuepvi37E7hU+bwZyYSoqqogVWxRyNbx7LMXnJ/PKMUhAn7a\n" +
                                        "kqV84/z//IdXftH6+IULC2B39eDu/OiprxprbxvbaMKC2ZGGHU3bakh3Ebz1WK/Z08ZUbnv9RrVN\n" +
                                        "U3GrbS22cZXFWH+ai3KojHd6crh6QrjaiHuno3rTVMOLSotxR3D9V3BHv6pJ86ITW+O3pBSxjVdu\n" +
                                        "d0XjpVr3tIsD7HSBGq42CAbvHL5plDNVCtkJxR5BfwGik5SQ602orAFNsQ97TWbJGXvyBPhGDeNy\n" +
                                        "9USoay4RLbYUArvdRtmwTpWbSnupYKnR/9dWRkLJWYWnRrOsR3ZfKYrCU7cJpu6kGmurj4RgmwJy\n" +
                                        "yI2nrwhhQYyH5LympPyd8+/9x5d/mdr4pQoLoJ2f3LXWfc0YOy3G4CYZM2xZP3rE8ektzGQOEioJ\n" +
                                        "L++1iZhKXxnZmHXFYF3NTbSG4kw9vXRcdtbWlHvtvUSKCmed7ipVqGmR+S0mn/4nNCfPYaxAgsKO\n" +
                                        "IjBpduQ+4FuvoQlZP9hUHQTb+QLTLjCuVTmV94p4Wy34YgykQplMYSiYeIGrzTe1OIuIQhGStWBT\n" +
                                        "JjuLvX5LAylTPc0oEJWZMC6r4xDo+44xX6eo4zBYaFpdSIuojsAaU31J9e8pZeRa6aOFq1BIkaw+\n" +
                                        "8hScbxDb4mjwUljc6ICO3dUBRbpV2my/0i/vve++6p3PL11Y/eZ82R5e/3Yx/E7BTiezlmivyF3k\n" +
                                        "9PgQd+0Ua2Y69WTlapPz3vZ5jI6ze2YZFKM9hPU66TjvFWGHeg1oIKW1TmkoVfZVrIeDz+Of+DUK\n" +
                                        "DTZcUmJQlNoBpmDNgEkbrNOtv0u6utHkUh1EmsUB+BbbeCXXWYtttZl3aOB6GgbaxSFpt8PGnRL/\n" +
                                        "6klQiq6CSvU/FTNQpgs4uqlsixTwhX0+YF1dIiIM3Y4QQgVLURtso3CjayOtBxLklHDOVbsj9YoX\n" +
                                        "rIpvK/amvVe19i66IM9ZIQivXxwxFt/2zI63XJ6xstnfuXjt7r1fti5+6cIC6NcPz6anz/ypt83L\n" +
                                        "1ltoAtbAcLVi6mFy/RQRvXpMNelgvA4Luour9N0xtaGUonq8Pa2jEvOMyr1HIh018SH7E5g9S3HX\n" +
                                        "yLstJW73cWnqapIpEjE541xPzommbSoVRq8Kycrlb6YL/GSOs8of18nwsZDTOe33Ykm42QkSBRnW\n" +
                                        "gMrkUsmazmodjbFYMaSjU5geUmKAqFyolNM+DVXzexLddqupEkZBU4pm4IgI3iW81x5LilSTOuXS\n" +
                                        "qz+5A+rpShmp7MrEEKVSS71Kc05QIiUVcgnceMayuoi/8fD7f/5Tqcbv9/lACgugX7557+jJF+4X\n" +
                                        "619qZpYsHcMg7F47o3GG2fUnCEOAIegb5sf9mFHulK36PmfBOe2dfIP16q9ZKi8bSnUdNvWNBJiT\n" +
                                        "Js+RJtfq1CQjWRkDeOdADFIiIoZ20hGHnrZpVNyQVaeoy2UoxtHM5ljbgG/0GnKNfsheWbR4jQyJ\n" +
                                        "2TA5vkFIAv0llowzM5xvaFqrp4RY8hNPY21LCYEiSW040RNY6gff73YMuw49x6saSapmuhQwCec1\n" +
                                        "LGvkc1lnK+IvIA6DDj6mmrxVgoIWmLd7yb5y9TNGAmEYOH6ar/3wT+79XI7V+3k+sMIC2Jy/9urx\n" +
                                        "k1+6b5ryktBhbWaQRLxccnA0p9Aiu4EyDJRth00dy8aKAAAP1UlEQVQBJzU5oUCxrRqNmImmZElC\n" +
                                        "YqSEAkMPoYE4Iecps8MbHN04pQuWwT2Lmd7AmVpwVvBOexM3aUcmCIWMyYbGD0jqFa9xaiQ7ng4l\n" +
                                        "RXUrtC3N4QHGtTjjKdbpkEFV/wDON9jGMISB6eF1coI2RxqbtZfKmpUT2znp4JRS9OTMkis2VvUd\n" +
                                        "Rj0Qdlcb3dcxshTMXnKPsdgiTKZJdYY51ybeqa12Vr6XaOSpXoTWqgS1IvIjTRtQtkYNhRImXzv7\n" +
                                        "3o9f+SBr4QMtLID1g79+dXHjs6fNxHwlyo7Ge0KfiBcXzL3G1pUYkNDTXa6QtWI2pmSuffof0S5u\n" +
                                        "0PoWKxb6SFrvMMsNcd2T7SFiF0iZ0LQt85tTBjkilmOayZycNZneWIf3XjnexqtFowjk6ppiBxqf\n" +
                                        "CEFwvloy1sY2pwgZYtIF+2Q2Va2hUT2icWqxbX2jC2HUNyGEATNd6C4wdJB3kAKpFOTkBixO9DrO\n" +
                                        "aaRE7JH/nDND3xO7QVXjowFKGXeUuuejlH0DD+yZCIUqwMggpq3mHlVAYUzlYI3vxyhh152BFPl3\n" +
                                        "ZXj4npfL7/X5wAsLYHP2w28fPvX8qbfylZwCrpnQr3fsHl0wcdRgSFWrpE5Jgq0xPPH5L8L0AIdu\n" +
                                        "89PQI0OAMCg1xc2QpqVYAek5OD1gtzskoz1PkVzBTb+n/VpLXcRWO2yj/9RMdqSQ8V5tAVJUk5AS\n" +
                                        "dcsfcaSgr3E2nVQFeA0d8A7ndFosFQ7wziISCTjs7IS422FSR2xa5PRJnGlJaaiwR7XUHqk4Wdiu\n" +
                                        "N+SgVBvZH7FVjoWrWsuCbzKmradZTCg9RoUYJQvFTCqBT1U66m/1OLZXv65+X8A3SJf/8sOogQ+l\n" +
                                        "sADWb97/9vVPP3dfbHypCJhFSwwJWa3xVu1zRAw5RFIMOG+4/qnnyM7rFj8Ecr8jh0FJeZKUkz6d\n" +
                                        "kfKG/uFbHC4WLOVEVc1UvKzq43IcsCXVRlapJNbXZhxH43YKSso7muAMZA1TYrSvTpGMo5nPsE1T\n" +
                                        "9XvQNK3GpTij8AhQskpMYhbM8Q3yUEgHJ/ij68qlylkZDKAfblEufOgHtrutAqRSKcK2TnauWkI6\n" +
                                        "9R0zRWgnEecmlByVmmwMJunrL7ah4DAOnPX1a6nF+jh5l5Ihl6+RL/7th/X5f2iFBbB8441XT59/\n" +
                                        "7r4QXyInmqZhqG57VjI4XZJKUFXu8c0bcHCqvUmIyBDIoUPCoFCAZEK3obt8iBtgF+fI/AkNwrZ2\n" +
                                        "rwLIOWKKTlZKLymaD+QmqvjBYG1Pw6BgqXPq6jJqJ8cMGVqyKcShWjZNW5rGK7Sb6/pZyfO6Nqn9\n" +
                                        "jjGGEBViMAfHun5KSU+U8thJEGMhF2IfSGHQCZZRUeT2GgfvGz0dC8oqnWRwTomFSccUY6wqg0St\n" +
                                        "B6zT60957aOne2U9SPwa8dErH+Znbz/MLw7w+n/7s1d8O3vRT9qV8ZbJfI4/OWZbNGDTWXUO7PqB\n" +
                                        "7vKhoumVtTkmIKjZB9iYSJsV3oI0C/zJ87Qnt9S7tGSdhGTUE1fF8yhxMmqMIcaBnyBljtjC6GHu\n" +
                                        "qnTeeKM/7QjFZoVHUmD96CEXP7rHcPEI6TodLIpoX5Oq0ggQo/J8ZxrKpKGdz9Uye9QyUldVVZmd\n" +
                                        "JZFCoAxxD7OMCMtY8GPPpUZoerWBwTSVy1V0UrTVsE1hmlGBpF+smtOucogv0n+4RQUf8ok1PuvX\n" +
                                        "z86OP/vUt62T3xEp0wK0zRTJmZCCAoupkMjcfO45ckhIjsgwkIeBHHoVQ2AxjaM5eZbmU/8Yf/IU\n" +
                                        "afsQVwJmT62VqrQOI28QUwSLUx4WOtEZyXjWWMn7D0+kYJKeWEqOa4FWjfVzIvaBru+q3ZLDzac4\n" +
                                        "6xUSMApblCxgBOMMZdqoz2gulBQ1v7BSV9QWqEBSeycdGpSx2XjPZKKnqysq/9cwl6IiFhexTg1m\n" +
                                        "c8h7vUEpoq9dlDuvZ6quebLISvp8h3j2geBU7/Z8JIUFsH794dni00/+gbPmRWvt7SJKfzWNxzae\n" +
                                        "4hy567h1+zaDVJR+6MmD+sFLTmRbsIfPYG9+UVcqpVpPo32ILeikRKkYllRCmyp/dcavCQy5AbvE\n" +
                                        "SNiDiXrFlTrKi/pnuWk91fTHX3KiX28ou6g7Pw8FuxdciCRyjrotmExwKSHDoPvDLPVEpaLi0F1t\n" +
                                        "6LodOcUKwuowIW5SweCMaaY6gVpfYY4B67L2UKP/Vs5atFkDp6xvtU8zBkG+U67WX0Ee3vuoPu+P\n" +
                                        "rLAANm+cLzdvXrxy/KnrxnlzB69xvCD4ZkLTQDID82ZKiYnUbxn6LaXvdMprTzAnn2Owc5wzOD9V\n" +
                                        "QxDb1CVreWwQUqnOZi8YLUgVrRoHYgUTHV6WeyCRugzeL5FxiJlUQ7Pqu1o5YiENhDhgnaZcmFGg\n" +
                                        "iiaotbO5CmVDXz3VGclFgEKgEjPL8wtSUqWMNRUcbmZokmmNIpmMknr9s972GrKpJU1OsXowoLZP\n" +
                                        "YjSmVz1Pv1Gu3ngZ+l9q9/d+n4+0sMZn/dbF3cUzt77jKP/c2GZqnFNbbQy71YZutURCx7Be0m/W\n" +
                                        "yBBJtoVrv0pqDphMp9h2jtTpzRqrIoCK1RTnqe5Z+mGnoGudogkRGvOBhhmwhhIqUg1UEw8TEkb0\n" +
                                        "gy5WA7yxftxs6kmQMrnvcY3H+KnmOJPAe5hO1aF4UF68GT1OUf5VAbbbLd1mjRNtsAWHcS22mWgg\n" +
                                        "pwRAsO2EfeXnBCbS+kF93L0hxYjJqixEjJL7aFaZ/FtlffbKx/EZfyyFBbB58/yeu3X8+xNnf8N6\n" +
                                        "exvvVerkHFmEbdcRo/ZfnbGY089j57cqxaapxDupOA/agNdG3BijdJZqpqE/+ZmSo+7VMCpsKOBr\n" +
                                        "YWnimE6QpRRFSDFk48BN6rWiv6x11e5ar544BKw3+InG4vnJVCVcQ48dkfSiC+Xx9ZYsrC8vicOg\n" +
                                        "f19leSh7tUraqomIa6d1M6DiXErG+VRXqAaLI6eBSeuQZIhJvpM68xXCxS9E0vsgno+tsAD6s2W/\n" +
                                        "fuvilcMnj40Y86K1duq801XJpMFOvNpEz5/Azz8PRfDTGaOp/TsLq1RrxT1VpKR9b1WyFpYp49So\n" +
                                        "0IQxDscGw6Aen/AYzQ6KkCfjKbbB2AZXVcfKfapczqJRbCUL1kKzmOLbCfQJJEJR8apGtuhrzTkT\n" +
                                        "dh3b1ZUugyvFuRSD96OyGXIacNbipuqLVSRVy0mDa6I6KItgfUORAe9kFYfyzX6ze/mjvvp+8vlY\n" +
                                        "C2t81mfLu4e3jv9AcvmsKeUFyFUTZyiNxc+/QCgTsOAbtaqUnPdjeCkCOWJJFEkYUx0XKtSgnCaw\n" +
                                        "fkIZFW9SEAkUl2hMj1gPppCzYFMhJuU8mZIwTLQQXV3oZhBvMZKrZKrgcJj5hOPT6xjjkBSRlKu3\n" +
                                        "mk5zxSo92EhkfXlO3HV1YqAqdZQQ6bxTvCkF/XPNQk/anChlQCTRtgFMDWfXtc4fiuGlq7Or96Wm\n" +
                                        "+bCeDx3Heq/P2av37j34yx++VNziq1L8fRVRBoo7QJiDSVhjSUFPAisJK1nxpJz2zesIG4y2i85Y\n" +
                                        "nLN6TcIeJxv9GfaYkK0UAMNekjVOb6D4Uq5mH8Yqd2r8fWMMxRuObl7X1UkMSE61nyv773HkRHWb\n" +
                                        "jn43UL9EnQQfWzWO9pWg6PlI1zFGOQ9I0diSYiml3M8pfHV579FLy3vLex/Rx/WuzyemsMbn7NXv\n" +
                                        "3X37ez+4bdujb1Dmq8IhUcCaWK+LqLwuKZRaYKodq/mF77BCfOy9oB9SqX2OEmpUQCH5cfkoa7Pq\n" +
                                        "/Mw7PDlH4HKMCRmvQeqUaGC2mOPnC5JU1+ac9gqgMXswx0ToBpaPLvZNeCmKXTmr+Jqt2sfxGdkI\n" +
                                        "quQpe7VyGlgZsd+4+MHZ7eW987sf8cf0rs8n4ir8ac/67MHdm1/6wh9krq8kuhet9dNSpKZkyP46\n" +
                                        "lOqfbpwH32raV4qK/4hOUdrAs792QJe/lISzBec3GK9JWaby7HPMkA3JCMVOqqHZ3zUooy62aSzX\n" +
                                        "nnmGtplVa/CAyTKedRWiLKQcWZ69Teo6nn6mZXHdsH3kwQaK8eC98s8w5Dxo8bdTXedIQlKkpLTK\n" +
                                        "OXyzl83v7t56+Im49n7a84ktLIDlvTeW2zf++m5/6wu/P/VuMCIvImlaJCoQWKRSlJW6grGVJ65C\n" +
                                        "BkRqjo26x4y0ZkpWZN5ouKb3W6Uql9oTidTCkmrZPcHYlnHkVzirAqYlMz895vCJJxWCGDodFqrN\n" +
                                        "kd7IhZJ6dpeP2K5WUIR+F/jyP21IxrK+yOCm1S9BtYWazFXqfrNBJK3i0H0z5uG3ZXX/2/T98ue+\n" +
                                        "eR/z84kurP1z9v2+//H/vjt98ou/X0p5u4j8ShE5MaUoE9NXh0F0SVsk6k931tNMRPR6sQbn7N5J\n" +
                                        "puQeS6Zp+lpYoqQ5KZQokIVshMIEzJgzY/fEQSmCmViefv7zMDlC4oDEYU8zLiL7pIjYd6zPHmiA\n" +
                                        "pmSSFB69LnzuK1O2nSF0BmfbypIY1IuLgmna+4L9Rrd6+2W5+Ntv0y8/1mnvvT7m3f+XT+YzfeE3\n" +
                                        "XzamvOSs+WcYi2+mtb/SMb3koap6rU5naAE2zRTjdLGcuw0lrTg43GHbSgNOqpyJm0BJSR1lWFDc\n" +
                                        "SfWS0OHAGkOxwvGTT3Dz9ucIGfL6qjq3sE8wtVbXU8uztxmu1rrfK5oJ6OyU9rDjmV855I3XLLnT\n" +
                                        "jMGcVkiWPyw5fSs++ptXPu73+hd5/t4W1v65fedkNpu87J1/2frm15XgF7GlPMavgFJEha5+ohrF\n" +
                                        "nJF+SwmXLBZrfKOm+pIFkwrDplfjEYlkFmCPlVyH5t0YCu3Uc/255/AHR9AFJHSK8BurC20AEXZX\n" +
                                        "G64ePdJQcjRYylYk32JpFjA7zPS79i/6XXll0Vy9srx37xN91b3b8/e/sN7xTF/4zdvO5K9TzB2T\n" +
                                        "5dfHnkjqKgdrMaapi1zlQpGWzCbnGqtWErlkbIRhOyAxacCTzDDmuGYpq3mHWLh26wZHT32GJJYy\n" +
                                        "bCEGKKmKYqpYImWuHl7Q7zYUEW3ErVFDEOspJf2Fc/5bSHqlP/v+vY/3Hfzgnn9QhfV3ntt3Tuau\n" +
                                        "vWN8uVNyuIPkXy/GYo3aMlqrFBbJGw7aB+oVL6Hy39Vzq0RDyoGcZog9QlypiRCJSdNy84Uv0MwO\n" +
                                        "SZudMk1TZvQzzZKwUui3GzbLVU0ogwJ/4Zy9W5y7m0O4y/Lv98n0s55/uIX1k8/tL5+0tHecnd5x\n" +
                                        "zn/ZOHs7p/AZV9YczC7JWCzqo65X4Q6yVee+PCObQ7XBLgZrCqfPPsPi1tOUBLLrVPmcsjrjFSg5\n" +
                                        "YVK+v1pe3hu67lURuQv8gy2kn3z+/ymsn/Esbn/py4cHqxOhvWNLvJ2Nvc2QCbstZEeSeDuEyWeK\n" +
                                        "Owaj6pr58eH90889f8+bBTll0m6DkXAvS7oncDentHz46t2PhFD3SX3+L8rVE3gsHblGAAAAAElF\n" +
                                        "TkSuQmCC\n";
                                title[count] = list.get(j).getTitle();
                                date[count] = list.get(j).getCreatedAt();
                                file[count] = list.get(j).getFile();
                                letterofAdapterList.add(new LetterofAdapter(friendImg[count], title[count], date[count], file[count]));
                                count++;
//                                for (int i = 0; i < userList.size(); i++){
//                                    if (list.get(j).getPost_id().equals(userList.get(i).getId())){
//                                        friendImg[count] = userList.get(i).getPhoto();
//                                        title[count] = list.get(j).getTitle();
//                                        date[count] = list.get(j).getReceive_date();
//                                        file[count] = list.get(j).getFile();
//                                        Toast.makeText(getApplicationContext(), "success get the Public letter" + title, Toast.LENGTH_SHORT).show();
//                                        letterofAdapterList.add(new LetterofAdapter(friendImg[count], title[count], date[count], file[count]));
//                                        count++;
//                                    }
//                                }
                            }
                        }
                    }
                    adapter.setLetterofAdapterList(letterofAdapterList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 将公开信件加载到listview上
     * ListView编程的一般步骤:
     * 1）在布局文件中声明ListView控件
     * 2) 使用一维或多维动态数组保存ListView要显示的数据
     * 3) 构建适配器Adapter,将数据与显示数据的布局页面绑定
     * 4）通过setAdapter()方法把适配器设置给ListView
     */
    private void loadPublicLetter(){
        adapter = new LetterAdapter(letterofAdapterList, getActivity());//初始化自定义Adapter
        lv.setAdapter(adapter);//填充listview
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
                popWindow.showPopupWindow(getActivity().findViewById(R.id.iv_more_letter));
            }
        });
    }
}
