package com.enliple.keyboard.Cipher;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.enliple.keyboard.ui.common.SharedPreference;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class E_Cipher {
    static byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    private static volatile E_Cipher INSTANCE;
    public static String ivSpec = "";
    public static E_Cipher getInstance() {
        if ( INSTANCE == null ) {
            synchronized ( E_Cipher.class) {
                if ( INSTANCE == null)
                    INSTANCE = new E_Cipher();
            }
        }
        return INSTANCE;
    }

    private E_Cipher() {
        ivSpec = Key.KEY_CR.substring(0, 16);
    }
/**
    public static String Encode(Context context, String str) {
        try {
            String k = SharedPreference.getString(context, Key.KEY_SEC);

            byte[] data = k.getBytes();
            SecretKey key = new SecretKeySpec(data, "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivSpec.getBytes()));

            byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
            String eStr = new String(Base64.encodeBase64(encrypted));
            return eStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String Decode(Context context, String str) {
        try {
            String k = SharedPreference.getString(context, Key.KEY_SEC);

            byte[] data = k.getBytes();
            SecretKey key = new SecretKeySpec(data, "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivSpec.getBytes()));

            byte[] byteStr = Base64.decodeBase64(str.getBytes());

            return new String(c.doFinal(byteStr), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
**/
    public static String Encode(Context context, String str) throws Exception {
        String key = SharedPreference.getString(context, Key.KEY_SEC);
        byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        String encodeStr = Base64.encodeToString(cipher.doFinal(textBytes), 0);
        LogPrint.d("skkim uuid encode :: " + encodeStr);
        return Base64.encodeToString(cipher.doFinal(textBytes), 0);
    }



    public static String Decode(Context context, String str) throws Exception {
        String key = SharedPreference.getString(context, Key.KEY_SEC);
        LogPrint.d("decode key :: " + key);
        byte[] textBytes = Base64.decode(str, 0);
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        String decodeStr = new String(cipher.doFinal(textBytes), "UTF-8");
        LogPrint.d("skkim uuid decode :: " + decodeStr);
        return new String(cipher.doFinal(textBytes), "UTF-8");
    }
}
