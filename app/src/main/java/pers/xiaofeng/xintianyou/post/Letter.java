package pers.xiaofeng.xintianyou.post;

import cn.bmob.v3.BmobObject;

/**
 * @author：廿柒
 * @description：信件类
 * @date：2020/4/20
 */
public class Letter extends BmobObject {

    private String post_id; //寄信人id
    private String receive_id; //收信人id
    private String title; //信件主题
    private String post_date; //信件邮寄日期
    private String receive_date; //信件接收日期
    private String isRead; //信件读取状态(1:已读；0：未读)
    private String isDelete; //信件删除状态（1：已删除；0：未删除）
    private String isPublic; //信件是否可见
    private String file; //信件内容

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getReceive_id() {
        return receive_id;
    }

    public void setReceive_id(String receive_id) {
        this.receive_id = receive_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getReceive_date() {
        return receive_date;
    }

    public void setReceive_date(String receive_date) {
        this.receive_date = receive_date;
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

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    //Bmob保存数据时指定表名为“letter”，若不指定，则默认为“Letter”
    public Letter(){
        this.setTableName("letter");
    }
}
