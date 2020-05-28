package pers.xiaofeng.xintianyou.letter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pers.xiaofeng.xintianyou.R;

/**
 * @author：廿柒
 * @description：自定义书信圈适配器
 * @date：2020/4/20
 */
public class LetterAdapter extends BaseAdapter {

    //自定义适配器的信件动态数组及上下文
    public List<LetterofAdapter> letterofAdapterList;
    public Context context;

    public LetterAdapter(List<LetterofAdapter> letterofAdapterList, Context context) {
        this.letterofAdapterList = letterofAdapterList;
        this.context = context;
    }

    public List<LetterofAdapter> getLetterofAdapterList() {
        return this.letterofAdapterList;
    }

    public void setLetterofAdapterList(List<LetterofAdapter> letterofAdapterList) {
        this.letterofAdapterList = letterofAdapterList;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.letterofAdapterList.size();
    }

    @Override
    public Object getItem(int position) {return null;}

    public LetterofAdapter getLetterofAdapter(int position) {
        return this.letterofAdapterList.get(position);
    }

    public void addCoupons(List<LetterofAdapter> list) {
        this.letterofAdapterList.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //内部类 包含listview的控件
    class ViewHolder{
        ImageView image;
        TextView title;
        TextView date;
        TextView file;
    }

    /**
     * 功能：获取自定义适配器的填充物控件 并且为之赋值（纯属自己理解内容）
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View tv = View.inflate(context, R.layout.mylistview_letter,null);
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,100);
        tv.setLayoutParams(param);
        ViewHolder viewHolder= new ViewHolder();
        if (convertView == null) {
            viewHolder.image = (ImageView) tv.findViewById(R.id.friend_img);
            viewHolder.title = (TextView) tv.findViewById(R.id.letter_title);
            viewHolder.date = (TextView) tv.findViewById(R.id.letter_date);
            viewHolder.file = (TextView) tv.findViewById(R.id.letter_file);
            tv.setTag(viewHolder);
        } else {
            tv = convertView;
            viewHolder = (ViewHolder) tv.getTag();
        }

        //初始化信件端头像
        String imagePath = letterofAdapterList.get(position).getImage();
        //如果路径为空，则显示默认图片
        try {
            if (imagePath.isEmpty()) {
                viewHolder.image.setImageResource(R.drawable.touxiang);
            } else {
                //将String对象转换成Bitmap对象并设置成当前的头像
                try {
                    byte[] bitmapArray;
                    bitmapArray = Base64.decode(imagePath, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
                    viewHolder.image.setImageBitmap(bitmap);
                } catch (Exception a) {
                    a.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //初始化listview视图上的各个控件
        viewHolder.title.setText(letterofAdapterList.get(position).getTitle());
        viewHolder.date.setText(letterofAdapterList.get(position).getDate());
        viewHolder.file.setText(letterofAdapterList.get(position).getFile());
        return tv;
    }

    public void refreshCoupons(List<LetterofAdapter> list) {
        List<LetterofAdapter> letterList = new ArrayList<>();
        for (LetterofAdapter f: list) {
            if (!this.letterofAdapterList.contains(f)) {
                letterList.add(f);
            }
        }
        this.letterofAdapterList.addAll(0, letterList);
        this.notifyDataSetChanged();
    }


    public void refreshthingList(List<LetterofAdapter> list) {
        List<LetterofAdapter> letterList = new ArrayList<>();
        for (LetterofAdapter f: list) {
            if (!this.letterofAdapterList.contains(f)) {
                letterList.add(f);
            }
        }
        this.letterofAdapterList.addAll(0, letterList);
        this.notifyDataSetChanged();
    }
}
