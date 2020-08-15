package com.doopp.agar.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryUtil {

    /**
     * md5 hash
     *
     * @param str 目标字符
     * @return String
     */
    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * sha256 hash
     *
     * @param str 目标字符
     * @param salt 加盐
     * @return String
     */
    public static String sha256(String str, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
}

