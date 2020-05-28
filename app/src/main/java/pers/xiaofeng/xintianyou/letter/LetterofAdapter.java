package pers.xiaofeng.xintianyou.letter;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author：廿柒
 * @description：书信圈适配器的信件类
 * @date：2020/4/20
 */
public class LetterofAdapter implements Serializable {

    private String image; //信件标志图
    private String title; //信件主题
    private String date; //收信日期
    private String file; //信件内容

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LetterofAdapter)) return false;
        LetterofAdapter letterofAdapter = (LetterofAdapter) o;
        return image.equals(letterofAdapter.image) &&
                title.equals(letterofAdapter.title)&&
                date.equals(letterofAdapter.date);
    }

    public LetterofAdapter(String image, String title, String date, String file) {
        this.image = image;
        this.title = title;
        this.date = date;
        this.file = file;
    }

    public LetterofAdapter(){};

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    @Override
    public int hashCode() {
        return Objects.hash(image, title, date);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
