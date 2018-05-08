package com.igorronner.irloginbackup.models;

import com.igorronner.irloginbackup.utils.MD5GenratorUtils;

/**
 * Created by IgorR on 30/07/2017.
 */

public class FirebaseUser {

    private String uuid;
    private String email;
    private String password;
    private String name;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password){
        this.password = MD5GenratorUtils.getMD5(password);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}