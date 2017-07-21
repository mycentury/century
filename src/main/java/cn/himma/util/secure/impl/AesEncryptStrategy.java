package cn.himma.util.secure.impl;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import cn.himma.util.secure.EncryptMethod;
import cn.himma.util.secure.IEncryptStrategy;

@Service
public class AesEncryptStrategy implements IEncryptStrategy {
    @Override
    public String encrypt(String source, String key) throws Exception {
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keyspec);
        byte[] encrypt = cipher.doFinal(source.getBytes());
        return new BASE64Encoder().encode(encrypt);
    }

    @Override
    public String decrypt(String source, String key) throws Exception {
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, keyspec);
        byte[] decodeBuffer = new BASE64Decoder().decodeBuffer(source);
        byte[] decrypt = c.doFinal(decodeBuffer);
        return new String(decrypt);
    }

    @Override
    public EncryptMethod getEncryptMethod() {
        return EncryptMethod.AES;
    }
}