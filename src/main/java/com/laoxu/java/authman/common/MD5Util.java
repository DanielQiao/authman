package com.laoxu.java.authman.common;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *  md5工具类
 */
public class MD5Util {
    public static final int time = 5;

    public static final String SALT = "springsecurity";

    /**
     * 密码加密方法
     *
     * @param password
     * @return
     */
    public static String encode(String password) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }
        try {
            for (int i = 0; i < time; i++) {
                byte[] bytes = digest.digest((password + SALT).getBytes("UTF-8"));
                password = String.format("%032x", new BigInteger(1, bytes));
            }
            return password;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
        }
    }

    public static String md5Password( String password, int hashIterations){
        /*SecureRandomNumberGenerator secureRandomNumberGenerator=new SecureRandomNumberGenerator();
        String salt= secureRandomNumberGenerator.nextBytes().toHex();
        System.out.println("=====salt: "+salt);*/
        //组合username,两次迭代，对密码进行加密
        String password_cryto = new Md5Hash(password,null,hashIterations).toString();

        return password_cryto;
    }

    public static void main(String[] args) {
        System.out.println(MD5Util.md5Password("123",2));
    }
}
