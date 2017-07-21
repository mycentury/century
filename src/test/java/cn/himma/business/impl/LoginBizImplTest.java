package cn.himma.business.impl;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;

import org.junit.Assert;
import org.junit.Test;

import cn.himma.MockTest;
import cn.himma.dao.LoginMapper;
import cn.himma.entity.UserEntity;

public class LoginBizImplTest extends MockTest {
	
	@Mocked
	@Injectable
	private LoginMapper loginMapper;
	@Tested
	private LoginBizImpl loginBizImpl;
	
	@Test
	public void testLogin() {
		UserEntity user = new UserEntity();
		new Expectations() {
			{ 
				loginMapper.checkUser(user);
				result = false;
				times = 1;
			}
		};
		boolean login = loginBizImpl.login(user);
		Assert.assertEquals(false, login);
	}

}
