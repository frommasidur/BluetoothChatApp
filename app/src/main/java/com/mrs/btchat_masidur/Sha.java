package com.mrs.btchat_masidur;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha {
    public static String getSha256Hash(String data) {
        try {
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            digest.reset();
            return bin2hex(digest.digest(data.getBytes()));
        } catch (Exception ignored) {
            return null;
        }
    }
    public static String getSha1Hash(String data){
        try {
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            digest.reset();
            return bin2hex(digest.digest(data.getBytes()));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String bin2hex(byte[] data) {
        StringBuilder hex = new StringBuilder(data.length * 2);
        for (byte b : data)
            hex.append(String.format("%02x", b & 0xFF));
        return hex.toString();
    }
}
