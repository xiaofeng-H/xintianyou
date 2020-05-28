package pers.xiaofeng.xintianyou.main;

import cn.bmob.v3.BmobObject;

/**
 * @author：廿柒
 * @description：用户类 用于记录用户信息
 * @date：2020/3/17
 */
public class User extends BmobObject implements Cloneable{

    private String id;//用户id
    private String user_name;//昵称
    private String pwd;//密码
    private String signature;//签名
    private String gender;//性别
    private String photo;//头像

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    //Bmob保存数据时指定表名为“user”，若不指定，则默认为“User”
    public User(){
        this.setTableName("user");
    }

    /**
     * Java浅克隆
     * @return
     */
    @Override
    public Object clone() {
        User user = null;
        try{
            user = (User)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return user;
    }
}