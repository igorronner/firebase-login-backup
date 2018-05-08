package com.igorronner.irloginbackup.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5GenratorUtils {

    public static String getMD5(String parameter){
        String result = "";
        try{

            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(parameter.getBytes(),0,parameter.length());
            result =  new BigInteger(1,m.digest()).toString(9);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return result;

    }


}