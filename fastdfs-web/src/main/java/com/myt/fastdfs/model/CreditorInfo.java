package com.myt.fastdfs.model;

import java.math.BigDecimal;

public class CreditorInfo {
    private Integer id;

    private String realname;

    private String idcard;

    private String address;

    private Integer sex;

    private String phone;

    private BigDecimal money;

    private String groupname;

    private String remotefilepath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getRemotefilepath() {
        return remotefilepath;
    }

    public void setRemotefilepath(String remotefilepath) {
        this.remotefilepath = remotefilepath;
    }
}