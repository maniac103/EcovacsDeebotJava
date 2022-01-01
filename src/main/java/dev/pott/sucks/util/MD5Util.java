package dev.pott.sucks.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    private MD5Util() {
        //Prevent instantiation of util class
    }

    public static String getMD5Hash(String input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        md.update(input.getBytes());
        byte[] hash = md.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            if ((0xff & b) < 0x10) {
                hexString.append("0").append(Integer.toHexString((0xFF & b)));
            } else {
                hexString.append(Integer.toHexString(0xFF & b));
            }
        }
        return hexString.toString();
    }
}
