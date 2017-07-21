package cn.himma.util.secure.impl;

import org.springframework.stereotype.Service;

import cn.himma.util.secure.EncryptMethod;
import cn.himma.util.secure.IEncryptStrategy;

@Service
public class NoneEncryptStrategy implements IEncryptStrategy {
    @Override
    public String encrypt(String source, String key) {
        return source;
    }

    @Override
    public String decrypt(String source, String key) {
        return source;
    }

    @Override
    public EncryptMethod getEncryptMethod() {
        return EncryptMethod.NONE;
    }
}