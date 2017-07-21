package cn.himma.util.secure.impl;

import org.springframework.stereotype.Service;

import cn.himma.util.secure.EncryptMethod;
import cn.himma.util.secure.IEncryptStrategy;
import cn.himma.util.secure.MD5Util;

@Service
public class Md5EncryptStrategy implements IEncryptStrategy {
    @Override
    public String encrypt(String source, String key) throws Exception {
        return MD5Util.encode(source);
    }

    @Override
    public String decrypt(String source, String key) throws Exception {
        throw new RuntimeException("md5不支持解密！");
    }

    @Override
    public EncryptMethod getEncryptMethod() {
        return EncryptMethod.MD5;
    }
}