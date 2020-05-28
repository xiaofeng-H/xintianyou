package pers.xiaofeng.xintianyou.receive;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.Serializable;
import java.util.Objects;


/**
 * @author：廿柒
 * @description：收信箱适配器的信件类
 * @date：2020/4/20
 */
public class ReceiveofAdapter implements Serializable {

    private String title; //信件主题
    private String date; //收信日期
    private String isRead; //信件读取状态
    private String isDelete; //信件删除状态
    private String file; //信件内容

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReceiveofAdapter)) return false;
        ReceiveofAdapter receiveofAdapter = (ReceiveofAdapter) o;
        return title.equals(receiveofAdapter.title)&&
                date.equals(receiveofAdapter.date)&&
                Objects.equals(isRead, receiveofAdapter.isRead)&&
                Objects.equals(isDelete, receiveofAdapter.isDelete)&&
                file.equals(receiveofAdapter.file);
    }

    public ReceiveofAdapter(String title, String date, String isRead, String isDelete, String file) {
        this.title = title;
        this.date = date;
        this.isRead = isRead;
        this.isDelete = isDelete;
        this.file = file;
    }

    public ReceiveofAdapter(){};

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    @Override
    public int hashCode() {
        return Objects.hash(title, date, isRead, isDelete, file);
    }


}
