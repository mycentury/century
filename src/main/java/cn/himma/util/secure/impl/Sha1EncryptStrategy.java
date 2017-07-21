package cn.himma.util.secure.impl;

import java.security.MessageDigest;

import org.springframework.stereotype.Service;

import cn.himma.util.secure.EncryptMethod;
import cn.himma.util.secure.IEncryptStrategy;

@Service
public class Sha1EncryptStrategy implements IEncryptStrategy {
    @Override
    public String encrypt(String source, String key) throws Exception {
        MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
        digest.update(source.getBytes());
        byte messageDigest[] = digest.digest();
        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        // 字节数组转换为 十六进制 数
        for (int i = 0; i < messageDigest.length; i++) {
            String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
            if (shaHex.length() < 2) {
                hexString.append(0);
            }
            hexString.append(shaHex);
        }
        return hexString.toString();
    }

    @Override
    public String decrypt(String source, String key) throws Exception {
        throw new RuntimeException("md5不支持解密！");
    }

    @Override
    public EncryptMethod getEncryptMethod() {
        return EncryptMethod.SHA1;
    }
}