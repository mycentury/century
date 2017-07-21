/**
 * 
 */
package cn.himma.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.himma.business.ILoginBiz;
import cn.himma.dao.LoginMapper;
import cn.himma.entity.UserEntity;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月20日
 * @ClassName LoginService
 */
@Service
public class LoginBizImpl implements ILoginBiz {

	@Autowired
	private LoginMapper loginMapper ;
	
    /**
     * @param user
     * @return
     */
    @Override
    public boolean login(UserEntity user) {
        boolean result = loginMapper.checkUser(user);
		return result;
    }

}
