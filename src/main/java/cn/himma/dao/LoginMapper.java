package cn.himma.dao;

import org.springframework.stereotype.Service;

import cn.himma.entity.UserEntity;

@Service
public class LoginMapper {
	public boolean checkUser(UserEntity user){
		boolean result = Math.random()>0.5?true:false;
		return result;
	}
}
