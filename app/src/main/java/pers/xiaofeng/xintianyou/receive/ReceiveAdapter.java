package pers.xiaofeng.xintianyou.receive;

import android.content.Context;
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
 * @description：收信箱适配器
 * @date：2020/4/20
 */
public class ReceiveAdapter extends BaseAdapter {

    //自定义适配器的信件动态数组及上下文
    public List<ReceiveofAdapter> receiveofAdapterList;
    public Context context;

    public ReceiveAdapter(List<ReceiveofAdapter> receiveofAdapterList, Context context) {
        this.receiveofAdapterList = receiveofAdapterList;
        this.context = context;
    }

    public List<ReceiveofAdapter> getReceiveofAdapterList() {
        return this.receiveofAdapterList;
    }

    public void setReceiveofAdapterList(List<ReceiveofAdapter> receiveofAdapterList) {
        this.receiveofAdapterList = receiveofAdapterList;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.receiveofAdapterList.size();
    }

    @Override
    public Object getItem(int position) {return null;}

    public ReceiveofAdapter getReceiveofAdapter(int position) {
        return this.receiveofAdapterList.get(position);
    }

    public void addCoupons(List<ReceiveofAdapter> list) {
        this.receiveofAdapterList.addAll(list);
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
        View tv = View.inflate(context, R.layout.mylistview_receive,null);
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,100);
        tv.setLayoutParams(param);
        ReceiveAdapter.ViewHolder viewHolder= new ReceiveAdapter.ViewHolder();
        if (convertView == null) {
            viewHolder.image = (ImageView) tv.findViewById(R.id.receive_icon);
            viewHolder.title = (TextView) tv.findViewById(R.id.receive_title);
            viewHolder.date = (TextView) tv.findViewById(R.id.receive_date);
            viewHolder.file = (TextView) tv.findViewById(R.id.receive_file);
            tv.setTag(viewHolder);
        } else {
            tv = convertView;
            viewHolder = (ReceiveAdapter.ViewHolder) tv.getTag();
        }

        //根据信件读取状态初始化信件图标
        String letterStatus = receiveofAdapterList.get(position).getIsRead();
        if(letterStatus.equals("1"))
            viewHolder.image.setImageResource(R.drawable.letter_true);
        else
            viewHolder.image.setImageResource(R.drawable.letter_false);

        //初始化listview视图上的各个控件
        viewHolder.title.setText(receiveofAdapterList.get(position).getTitle());
        viewHolder.date.setText(receiveofAdapterList.get(position).getDate());
        viewHolder.file.setText(receiveofAdapterList.get(position).getFile());
        return tv;
    }

    public void refreshCoupons(List<ReceiveofAdapter> list) {
        List<ReceiveofAdapter> receiveList = new ArrayList<>();
        for (ReceiveofAdapter f: list) {
            if (!this.receiveofAdapterList.contains(f)) {
                receiveList.add(f);
            }
        }
        this.receiveofAdapterList.addAll(0, receiveList);
        this.notifyDataSetChanged();
    }


    public void refreshthingList(List<ReceiveofAdapter> list) {
        List<ReceiveofAdapter> receiveList = new ArrayList<>();
        for (ReceiveofAdapter f: list) {
            if (!this.receiveofAdapterList.contains(f)) {
                receiveList.add(f);
            }
        }
        this.receiveofAdapterList.addAll(0, receiveList);
        this.notifyDataSetChanged();
    }
}
